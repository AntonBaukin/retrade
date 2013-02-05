package com.tverts.api.core;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: api */

import com.tverts.api.Payload;


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

	public void       setPrevTx(Long prevTx)
	{
		this.prevTx = prevTx;
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
	public long         getMaxTx()
	{
		return maxTx;
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

		//?: {initial request} gte the maximum Tx
		if(prev == null)
			return de;

		//?: {the answer on the initial max-Tx request}
		if(prev.getObject() instanceof DumpEntities)
		{
			DumpEntities pd = (DumpEntities)prev.getObject();

			//?: {the server has no data} (almost impossible)
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

	protected long              maxTx;
	protected long              minKey;
	protected Map<Long, Object> objects =
	  new HashMap<Long, Object>();
}