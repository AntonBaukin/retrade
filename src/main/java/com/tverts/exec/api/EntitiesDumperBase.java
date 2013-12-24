package com.tverts.exec.api;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: api */

import com.tverts.api.core.DumpEntities;

/* com.tverts: system */

import com.tverts.system.SystemConfig;
import com.tverts.system.tx.TxPoint;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;

/* com.tverts: support */

import com.tverts.support.CMP;


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
		return CMP.eq(getEntityClass(), de.getEntityClass()) &&
		  CMP.eq(getUnityType(), de.getUnityType());
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

	/**
	 * This implementation generates the next Tx number.
	 * When dumping more than one entity type, set this
	 * number in the following requests to prevent
	 * referring entities of newer transactions.
	 */
	protected Object    selectTxBoundaries(DumpEntities de)
	{
		de.setMaxTx(TxPoint.getInstance().newTxn());
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
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("EntityClass", ut.getTypeClass());
		qb.setClauseFrom("EntityClass e");

		//~: select clause
		qb.setClauseSelect("e");

		//~: order by clause
		qb.setClauseOrderBy("e.id");


		//~: minimum key limit
		qb.getClauseWhere().addPart(
		  "e.id > :minPk"
		).
		  param("minPk", de.getMinPkey());

		//~: Tx boundaries
		qb.getClauseWhere().addPart(
		  "e.txn between :minTx and :maxTx"
		).
		  param("minTx", minTx).
		  param("maxTx", maxTx);

		//~: unity type
		qb.getClauseWhere().addPart(
		  "e.unity.unityType = :unityType"
		).
		  param("unityType", ut);

		//~: restrict the domain
		restrictDumpDomain(qb, de);

		//!: select
		return qb.buildQuery(session()).
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
		QueryBuilder qb = new QueryBuilder();

		//~: from clause
		qb.nameEntity("EntityClass", getUnityClass());
		qb.setClauseFrom("EntityClass e");

		//~: select clause
		qb.setClauseSelect("e");

		//~: order by clause
		qb.setClauseOrderBy("e.id");


		//~: minimum key limit
		qb.getClauseWhere().addPart(
		  "e.id > :minPk"
		).
		  param("minPk", de.getMinPkey());

		//~: Tx boundaries
		qb.getClauseWhere().addPart(
		  "e.txn between :minTx and :maxTx"
		).
		  param("minTx", minTx).
		  param("maxTx", maxTx);

		//~: restrict the domain
		restrictDumpDomain(qb, de);

		//!: select
		return qb.buildQuery(session()).
		  setMaxResults(getDumpLimit(de)).
		  list();
	}

	protected void      restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		//~: assume entity is a DomainEntity
		qb.getClauseWhere().addPart(
		  "e.domain = :domain"
		).
		  param("domain", tx().getDomain());
	}

	protected int       getDumpLimit(DumpEntities de)
	{
		return SystemConfig.getInstance().getDumpLimit();
	}


	/* configuration */

	private String unityType;
}