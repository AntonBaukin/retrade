package com.tverts.shunts;

/* standard Java classes */

import java.io.Serializable;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;

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