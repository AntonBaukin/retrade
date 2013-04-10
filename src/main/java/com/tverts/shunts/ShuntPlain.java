package com.tverts.shunts;

/* standard Java classes */

import java.io.Serializable;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;

/* com.tverts: support */

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
		catch( CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}


	/* protected: context handling */

	protected SelfShuntCtx ctx()
	{
		SelfShuntCtx ctx = SelfShuntPoint.getInstance().context();

		//?: {there is no context}
		if(ctx == null) throw new IllegalStateException(
		  "Self-Shunt Context is not assigned to the shunting thread!");

		return ctx;
	}

	private Domain         domain()
	{
		//?: {there is no domain key}
		if(ctx().getDomain() == null)
			throw new IllegalStateException(String.format(
			  "Self-Shunt Context with UID [%s] has no Domain key defined!",
			  ctx().getUID()
			));

		//~: load the domain
		Domain domain = bean(GetDomain.class).
		  getDomain(ctx().getDomain());

		//?: {not found it}
		if(domain == null)
			throw new IllegalStateException(String.format(
			  "Self-Shunt Context with UID [%s] refers Domain key [%d]" +
			  " that is not found!", ctx().getUID(), ctx().getDomain()
			));

		return domain;
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

	@SuppressWarnings("unchecked")
	protected <O> O   get(Class<O> c1ass, Serializable key)
	{
		return (O)session().get(c1ass, key);
	}

	@SuppressWarnings("unchecked")
	protected <O> O   load(Class<O> c1ass, Serializable key)
	{
		return (O)session().load(c1ass, key);
	}


	/* protected: logging */

	protected String  getLog()
	{
		return LU.getLogBased(SelfShuntPoint.LOG_SHARED, this);
	}
}