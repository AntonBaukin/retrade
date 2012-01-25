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
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}


	/* protected: HQL helping methods */

	protected Session session()
	{
		Session session = (sessionFactory != null)
		  ?(sessionFactory.getCurrentSession())
		  :(HiberPoint.session());

		//?: {has no session} illegal state
		if(session == null) throw new IllegalStateException(
		  "No Hibernate Session instance is bount to the " +
		  "Get-??? DAO strategy!");

		return session;
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