package com.tverts.hibery;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: endure (core) */

import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.NumericIdentity;
import com.tverts.endure.keys.KeysContext;
import com.tverts.endure.keys.KeysPoint;

/* com.tverts: hibery */

import com.tverts.hibery.keys.HiberKeysContextStruct;
import com.tverts.hibery.system.HiberSystem;

/* com.tverts: system transactions */

import com.tverts.system.tx.Tx;


/**
 * Singleton with static interface to access
 * system-wide primary database via Hibernate.
 *
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

	public Session        getSession()
	{
		SessionFactory sf = getSessionFactory();
		if(sf == null) throw new IllegalStateException();

		Session res = sf.getCurrentSession();
		if(res == null) throw new IllegalStateException(
		  "Spring @Transaction context was not opened! No Session!");
		return res;
	}

	/* public static: primary database connectivity support */

	public static Query   query(Session session, String hql, Object... replaces)
	{
		StringBuilder s = new StringBuilder(hql);

		for(int k = 0;(k + 1 < replaces.length);k += 2)
		{
			//~: the name to search
			String n = replaces[k].toString().trim();

			//~: the replacement
			String r = (replaces[k + 1] instanceof Class)
			  ?(((Class)replaces[k + 1]).getName())
			  :(replaces[k + 1].toString());

			//c: for all occurrences of the name
			for(int i = s.indexOf(n);(i != -1);)
			{
				//?: {the previous character is not a whitespace} skip
				if((i != 0) && !Character.isWhitespace(s.charAt(i - 1)))
				{
					i = s.indexOf(n, i + n.length());
					continue;
				}

				//?: {the next character is not a whitespace} skip
				if((i + n.length() < s.length()) &&
				   !Character.isWhitespace(s.charAt(i + n.length()))
				  )
				{
					i = s.indexOf(n, i + n.length());
					continue;
				}

				//!: do replace this occurrence
				s.replace(i, i + n.length(), r);

				//~: go to the next occurrence
				i = s.indexOf(n, i + r.length());
			}
		}

		return session.createQuery(s.toString());
	}


	/* public static: keys generation support */

	public KeysContext    keysContext(Object instance)
	{
		return keysContext(instance, getInstance().getSession());
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
	  (Tx tx, NumericIdentity instance, boolean fortest)
	{
		HiberPoint.getInstance().createPrimaryKey(
		  tx.getSessionFactory().getCurrentSession(), instance, fortest);
	}

	public void           createPrimaryKey
	  (Session session, NumericIdentity instance, boolean fortest)
	{
		//?: {already have primary key} do not force change
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
		if(instance.getPrimaryKey() != null)
			return (instance.getPrimaryKey() < 0L);

		if(instance instanceof DomainEntity)
			return isTestInstance(((DomainEntity) instance).getDomain());

		return false;
	}

	public static boolean isTestPrimaryKey(Long pk)
	{
		return (pk != null) && (pk < 0L);
	}


	/* private: primary database connectivity */

	private volatile SessionFactory sessionFactory;
}