package com.tverts.hibery;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* Spring Framework */

import org.springframework.orm.hibernate3.SessionFactoryUtils;

/* com.tverts: endure + hibery keys  */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.keys.KeysContext;
import com.tverts.endure.keys.KeysPoint;
import com.tverts.hibery.keys.HiberKeysContextStruct;

/* com.tverts: hibery + system */

import com.tverts.hibery.system.HiberSystem;
import com.tverts.system.tx.TxContext;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * TODO comment HiberPoint
 *
 * @author anton.baukin@gmail.com
 */
public class HiberPoint
{
	/* public: Singleton */

	public static HiberPoint getInstance()
	{
		return INSTANCE;
	}

	private static final HiberPoint INSTANCE =
	  new HiberPoint();

	protected HiberPoint()
	{}

	/* public: primary database connectivity */

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void           setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
		HiberSystem.getInstance().setSessionFactory(sf);
	}

	public Session        getSession(boolean allowCreate)
	{
		SessionFactory sf = getSessionFactory();
		if(sf == null) throw new IllegalStateException();

		return SessionFactoryUtils.getSession(sf, allowCreate);
	}

	/* public static: primary database connectivity support */

	/**
	 * Returns the Hibernate session currently bound to
	 * the execution context. Provides strict handling:
	 * if no session bound, an exception is raised.
	 *
	 * This session instance is obtained from primary
	 * session factory. The same factory is injected
	 * via Spring @Autowire.
	 */
	public static Session session()
	{
		return getInstance().getSession(false);
	}

	public static Session session(boolean allowCreate)
	{
		return getInstance().getSession(allowCreate);
	}

	public static Query query(Session session, String hql, Object... replaces)
	{
		for(int i = 0;(i + 1 < replaces.length);i += 2)
		{
			String name = replaces[i].toString().trim();
			String real = (replaces[i + 1] instanceof Class)
			  ?(((Class)replaces[i + 1]).getSimpleName()) //<-- short name!
			  :(replaces[i + 1].toString());

			hql = SU.replace(hql, SU.cats(" ", name, " ") ,
			  SU.cats(" ", real, " "));
		}

		return session.createQuery(hql);
	}


	/* public static: keys generation support */

	public KeysContext    keysContext(Object instance)
	{
		return keysContext(instance, session());
	}

	public KeysContext    keysContext(Object instance, Session session)
	{
		if(instance == null)
			throw new IllegalArgumentException();

		return new HiberKeysContextStruct(instance.getClass()).
		  setSavedInstance(instance).
		  setSession(session);
	}

	public static void    setPrimaryKey
	  (Session session, NumericIdentity instance)
	{
		HiberPoint.getInstance().
		  createPrimaryKey(session, instance, false);
	}

	public static  void   setPrimaryKey
	  (Session session, NumericIdentity instance, boolean fortest)
	{
		HiberPoint.getInstance().
		  createPrimaryKey(session, instance, fortest);
	}

	public static void    setPrimaryKey
	  (TxContext tx, NumericIdentity instance, boolean fortest)
	{
		HiberPoint.getInstance().createPrimaryKey(
		  tx.getSessionFactory().getCurrentSession(), instance, fortest);
	}

	public void           createPrimaryKey
	  (Session session, NumericIdentity instance, boolean fortest)
	{
		//?: {already have primary key} do not force change
		//!: THIS IS PROTOCOL
		if(instance.getPrimaryKey() != null)
			return;

		Object primaryKey = KeysPoint.facadeGenerator().
		  createPrimaryKey(keysContext(instance, session));

		if(!(primaryKey instanceof Long))
			throw new IllegalStateException();

		//?: {is a test instance}
		if(fortest)
			primaryKey = -(Long)primaryKey;

		instance.setPrimaryKey((Long)primaryKey);
	}

	public static boolean isTestInstance(NumericIdentity instance)
	{
		return (instance.getPrimaryKey() != null) &&
		  (instance.getPrimaryKey() < 0);
	}

	/* private: primary database connectivity */

	private volatile SessionFactory sessionFactory;
}