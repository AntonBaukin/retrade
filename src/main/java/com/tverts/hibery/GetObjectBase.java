package com.tverts.hibery;

/* standard Java classes */

import java.io.Serializable;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxPoint;

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

	@Autowired
	public void  setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public <T extends NumericIdentity> T
	             getNumeric(Class<T> c1ass, Long pk)
	{
		return (T) session().get(c1ass, pk);
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		Session session = null;

		TxContext tx = TxPoint.getInstance().getTxContext();
		if((tx != null) && (tx.getSessionFactory() != null))
			session = tx.getSessionFactory().getCurrentSession();

		if((session == null) && (sessionFactory != null))
			session = sessionFactory.getCurrentSession();

		return (session != null)?(session):HiberPoint.session();
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


	/* protected: Hibernate Session Factory reference */

	protected SessionFactory sessionFactory;
}