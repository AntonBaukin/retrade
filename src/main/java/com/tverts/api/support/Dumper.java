package com.tverts.api.support;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: api */

import com.tverts.api.Payload;
import com.tverts.api.core.DumpEntities;
import com.tverts.api.core.TwoKeysObject;


/**
 * Strategy of dumping the entities
 * updated on the server.
 */
public class Dumper
{
	/* public: constructor */

	public Dumper(Class entityClass, String unityType)
	{
		if(entityClass == null)
			throw new IllegalArgumentException();

		if(unityType != null)
			unityType = unityType.trim();

		this.entityClass = entityClass;
		this.unityType   = unityType;
	}


	/* public: Dumper (configuration) interface */

	public Class      getEntityClass()
	{
		return entityClass;
	}

	public String     getUnityType()
	{
		return unityType;
	}

	/**
	 * Dumper allows to select only the entities
	 * updated from the oldest transaction known.
	 *
	 * To select data delta set this property to the
	 * maximum Tx number of the data available from
	 * the previous dumps. Then, only the objects
	 * updated by the transactions having Tx greater
	 * would be selected.
	 */
	public Long       getPrevTx()
	{
		return prevTx;
	}

	public Dumper     setPrevTx(Long prevTx)
	{
		this.prevTx = prevTx;
		return this;
	}


	/* public: Dumper (execution) interface */

	public Map<Long, Object>
	                    getObjects()
	{
		return objects;
	}

	/**
	 * Returns the maximum transaction number
	 * got from the server. Save this value
	 * till the future dump operation to select
	 * only the modified objects.
	 */
	public Long         getMaxTx()
	{
		return maxTx;
	}

	/**
	 * When dumping several types of entities,
	 * set the maximum transaction number to
	 * restrict all the entities to the same
	 * period of the system life.
	 *
	 * Warning! This is actually needed, or
	 * broken-obsolete references may occur!
	 */
	public Dumper       setMaxTx(Long maxTx)
	{
		this.maxTx = maxTx;
		return this;
	}

	/**
	 * Invoke this method to create the next request
	 * continuing the previous one. Undefined result
	 * means all the entities are ready.
	 */
	@SuppressWarnings("unchecked")
	public DumpEntities next(Payload prev)
	{
		DumpEntities de = new DumpEntities();

		de.setEntityClass(getEntityClass());
		de.setUnityType(getUnityType());
		de.setMinTx(getPrevTx());

		//?: {initial request} get max Tx | got it provided
		if(prev == null)
		{
			//?: {Tx boundaries are provided} request the data
			if(getMaxTx() != null)
			{
				de.setMaxTx(getMaxTx());

				//~: set the lowest negative value to support test domains
				de.setMinPkey(this.minKey = Long.MIN_VALUE);
			}

			return de;
		}

		//?: {the answer on the initial max-Tx request}
		if(prev.getObject() instanceof DumpEntities)
		{
			DumpEntities pd = (DumpEntities)prev.getObject();

			//?: {the server has empty database}
			if(pd.getMaxTx() == null)
				return null;

			//!: send request for the first bunch of data
			de.setMaxTx(this.maxTx = pd.getMaxTx());

			//~: set the lowest negative value to support test domains
			de.setMinPkey(this.minKey = Long.MIN_VALUE);

			return de;
		}

		//?: {the server has no data more}
		if((prev.getList() == null) || prev.getList().isEmpty())
			return null;

		//~: process the object dumped
		for(Object obj : prev.getList())
		{
			Long key = !(obj instanceof TwoKeysObject)?(null):
			  (((TwoKeysObject)obj).getPkey());

			if(key == null)
				continue;
			if(key > minKey)
				minKey = key;

			objects.put(key, obj);
		}

		de.setMaxTx(this.maxTx);
		de.setMinPkey(minKey);

		return de;
	}


	/* strategy parameters */

	private Class  entityClass;
	private String unityType;
	private Long   prevTx;


	/* strategy state */

	protected Long              maxTx;
	protected long              minKey;
	protected Map<Long, Object> objects =
	  new HashMap<Long, Object>();
}