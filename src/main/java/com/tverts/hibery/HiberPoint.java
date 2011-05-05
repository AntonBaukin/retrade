package com.tverts.hibery;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;

/* Spring Framework */

import org.springframework.orm.hibernate3.SessionFactoryUtils;

/* com.tverts: endure, hibery + keys */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.keys.KeysContext;
import com.tverts.endure.keys.KeysPoint;
import com.tverts.hibery.keys.HiberKeysContextStruct;

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
		ConnectPoint.getInstance().setProvider(null);

		this.sessionFactory = sf;

		//?: {this factory provides implementation interface}
		if(sf instanceof SessionFactoryImplementor)
		{
			//~: remember the JDBC connections provider
			ConnectPoint.getInstance().setProvider(
			  ((SessionFactoryImplementor)sf).getConnectionProvider());
		}
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

	public void           newPrimaryKey(NumericIdentity instance)
	{
		newPrimaryKey(instance, session());
	}

	public void           newPrimaryKey(NumericIdentity instance, Session session)
	{
		Object primaryKey = KeysPoint.facadeGenerator().
		  createPrimaryKey(keysContext(instance, session));

		if(!(primaryKey instanceof Long))
			throw new IllegalStateException();

		instance.setPrimaryKey((Long)primaryKey);
	}

	/* private: primary database connectivity */

	private volatile SessionFactory sessionFactory;
}