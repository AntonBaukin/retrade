package com.tverts.exec.api;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: api */

import com.tverts.api.core.DumpEntities;

/* com.tverts: system */

import com.tverts.system.SystemConfig;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.query;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;


/**
 * Base class for executor of {@link DumpEntities} requests.
 * Concrete subclasses do wrap the entities selected with
 * the API analogues.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EntitiesDumperBase
       extends        ExecutorBase
{
	/* public: EntitiesDumperBase (bean) interface */

	public String getUnityType()
	{
		return unityType;
	}

	public void   setUnityType(String unityType)
	{
		this.unityType = unityType;
	}


	/* public: Executor interface */

	public Object execute(Object object)
	{
		if(!(object instanceof DumpEntities))
			return null;

		DumpEntities de = (DumpEntities)object;

		if(!isThatRequest(de))
			return null;

		if(de.getMaxTx() == null)
			return selectTxBoundaries(de);
		return selectWrapEntities(de);
	}


	/* protected: dumping */

	protected abstract  Object createApiEntity(Object src);

	/**
	 * Class of the database entity.
	 */
	protected abstract  Class  getUnityClass();

	/**
	 * Class of the API associated with the class
	 * of the database entity.
	 */
	protected abstract  Class  getEntityClass();

	protected boolean   isThatRequest(DumpEntities de)
	{
		return getEntityClass().equals(de.getEntityClass()) && (
		  ((getUnityType() == null) && (de.getUnityType() == null)) ||
		  ((getUnityType() != null) && getUnityType().equals(de.getUnityType()))
		);
	}

	@SuppressWarnings("unchecked")
	protected List      selectWrapEntities(DumpEntities de)
	{
		List sel = (de.getUnityType() == null)
		  ?(selectEntitiesByClass(de))
		  :(selectEntitiesByType(de));

		List res = new ArrayList(sel.size());

		for(Object src : sel)
		{
			Object obj = createApiEntity(src);
			if(obj != null)
				res.add(obj);
		}

		return res;

	}

	protected UnityType unityType(DumpEntities de)
	{
		return UnityTypes.unityType(
		  getUnityClass(), de.getUnityType());
	}

	protected Object    selectTxBoundaries(DumpEntities de)
	{

// select max(e.txn) from EntityClass e

		Number txn = (Number) query( session(),

		  "select max(e.txn) from EntityClass e",
		  "EntityClass", getUnityClass()
		).
		  uniqueResult();

		if(txn != null)
			de.setMaxTx(txn.longValue());
		else
			de.setMaxTx(0L);

		return de;
	}

	@SuppressWarnings("unchecked")
	protected List      selectEntitiesByType(DumpEntities de)
	{
		UnityType ut    = unityType(de);
		long      minTx = (de.getMinTx() == null)?(0L):
		  (de.getMinTx() + 1);
		long      maxTx = de.getMaxTx();

/*

select e from EntityClass e where
  (e.id > :minPk) and (e.domain = :domain) and
  (e.unity.unityType = :unityType) and
  (e.txn between :minTx and :maxTx)
order by e.id

 */
		return query( session(),

"select e from EntityClass e where\n" +
"  (e.id > :minPk) and (e.domain = :domain) and\n" +
"  (e.unity.unityType = :unityType) and\n" +
"  (e.txn between :minTx and :maxTx)\n" +
"order by e.id",

		"EntityClass", ut.getTypeClass()

		).
		  setLong     ("minPk",     de.getMinPkey()).
		  setParameter("domain",    tx().getDomain()).
		  setParameter("unityType", ut).
		  setLong     ("minTx",     minTx).
		  setLong     ("maxTx",     maxTx).
		  setMaxResults(getDumpLimit(de)).
		  list();
	}

	@SuppressWarnings("unchecked")
	protected List      selectEntitiesByClass(DumpEntities de)
	{
		long minTx = (de.getMinTx() == null)?(0L):(de.getMinTx() + 1);
		long maxTx = de.getMaxTx();

/*

select e from EntityClass e where
  (e.id > :minPk) and (e.domain = :domain) and
  (e.txn between :minTx and :maxTx)
order by e.id

 */
		return query( session(),

"select e from EntityClass e where\n" +
"  (e.id > :minPk) and (e.domain = :domain) and\n" +
"  (e.txn between :minTx and :maxTx)\n" +
"order by e.id",

		"EntityClass", getUnityClass()

		).
		  setLong     ("minPk",  de.getMinPkey()).
		  setParameter("domain", tx().getDomain()).
		  setLong     ("minTx",  minTx).
		  setLong     ("maxTx",  maxTx).
		  setMaxResults(getDumpLimit(de)).
		  list();
	}

	protected int       getDumpLimit(DumpEntities de)
	{
		return SystemConfig.getInstance().getDumpLimit();
	}


	/* configuration */

	private String unityType;
}