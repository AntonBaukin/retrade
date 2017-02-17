package com.tverts.shunts;

/* Java */

import java.io.Serializable;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: aggregation */

import com.tverts.aggr.AggrService;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Plain Self Shunts are POJO targets for
 * {@link SelfShuntTarget} units.
 *
 * This implementation makes the definition
 * of a plain shunt look better.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ShuntPlain
       implements     Cloneable
{
	/* public: Cloneable interface */

	public ShuntPlain clone()
	{
		try
		{
			return (ShuntPlain)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw EX.wrap(e);
		}
	}


	/* protected: context handling */

	protected SelfShuntCtx ctx()
	{
		return EX.assertn(
		  SelfShuntPoint.getInstance().context(),
		  "Self-Shunt Context is not assigned to the shunting thread!"
		);
	}

	protected Domain       domain()
	{
		//?: {domain is directly set}
		if(domain != null)
			return domain;

		//?: {there is no domain key}
		EX.assertn(ctx().getDomain(), "Self-Shunt Context with UID [",
		  ctx().getUID(), "] has no Domain key defined!");

		//~: load the domain
		Domain domain = bean(GetDomain.class).
		  getDomain(ctx().getDomain());

		//?: {not found it}
		return EX.assertn(domain, "Self-Shunt Context with UID [",
		  ctx().getUID(), "] refers Domain key [",
		  ctx().getDomain(), "] that is not found!"
		);
	}

	private Domain domain;

	public ShuntPlain      setDomain(Domain domain)
	{
		this.domain = domain;
		return this;
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		return txSession();
	}

	protected Query   Q(String hql)
	{
		return session().createQuery(hql);
	}

	protected <O> O   get(Class<O> c1ass, Serializable key)
	{
		return session().get(c1ass, key);
	}

	protected <O> O   load(Class<O> c1ass, Serializable key)
	{
		return session().load(c1ass, key);
	}


	/* protected: shunt supporting methods */

	/**
	 * Waits for the asynchronous aggregation to complete.
	 */
	protected void awaitAggregation()
	{
		while(true)
		{
			int size = AggrService.size();

			if(size != 0)
			{
				LU.D(getLog(), "waiting for asynchronous aggregation ",
				  "having [", size, "] entries");

				try
				{
					Thread.sleep(10000L);
				}
				catch(Throwable e)
				{
					throw EX.wrap(e);
				}
			}
			else
			{
				LU.D(getLog(), "asynchronous aggregation completed!");
				break;
			}
		}
	}


	/* protected: logging */

	/**
	 * Returns log withing shunts packages as if this
	 * class were placed in there.
	 */
	protected String getLog()
	{
		return (log != null)?(log):
		  (log = LU.LB(SelfShuntPoint.LOG_SHARED, this.getClass()));
	}

	private String log;
}