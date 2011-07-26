package com.tverts.shunts;

/* standard Java classes */

import java.io.Serializable;

/* Spring Framework */

import org.springframework.beans.factory.annotation.Autowired;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;


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
		  "Self Shunt Unit!");

		return session;
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




	/* protected: Hibernate Session Factory reference */

	protected SessionFactory sessionFactory;

}