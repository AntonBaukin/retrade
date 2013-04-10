package com.tverts.hibery;

/* standard Java classes */

import java.io.Serializable;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: system (transactions) */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;


/**
 * Basic implementation of so-called 'Get Object' Spring Bean.
 * It implements Data Access Object pattern.
 *
 * Get objects may be a prototype, or a singleton beans.
 * Consider {@link GetObjectPrototypeBase} subclass to allow
 * direct session references stored in prototype beans.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GetObjectBase
{
	/* public: access Session Factory */

	@SuppressWarnings("unchecked")
	public <T extends NumericIdentity> T
	                  getNumeric(Class<T> c1ass, Long pk)
	{
		return (T) session().get(c1ass, pk);
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		return txSession();
	}

	protected Query   Q(String hql, Object... replaces)
	{
		return HiberPoint.query(session(), hql, replaces);
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

	protected Query   QB(QueryBuilder qb)
	{
		return qb.buildQuery(session());
	}
}