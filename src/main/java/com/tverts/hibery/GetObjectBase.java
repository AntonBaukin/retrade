package com.tverts.hibery;

/* standard Java classes */

import java.io.Serializable;


/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Basic implementation of so-called 'Get Object' Spring Bean.
 * It implements Data Access Object pattern.
 *
 * Get objects may be a prototype, or a singleton beans.
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


	/* protected: helping methods */

	protected Query Q(String hql)
	{
		return sessionFactory.getCurrentSession().createQuery(hql);
	}

	@SuppressWarnings("unchecked")
	protected <O> O get(Class<O> c1ass, Serializable key)
	{
		return (O)sessionFactory.getCurrentSession().get(c1ass, key);
	}

	@SuppressWarnings("unchecked")
	protected <O> O load(Class<O> c1ass, Serializable key)
	{
		return (O)sessionFactory.getCurrentSession().load(c1ass, key);
	}


	/* protected: Hibernate Session Factory reference*/

	protected SessionFactory sessionFactory;
}