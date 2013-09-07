package com.tverts.hibery;

/* Hibernate Persistence Layer */

import com.tverts.support.LU;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: endure (core) */

import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.NumericIdentity;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: system transactions */

import com.tverts.system.tx.Tx;

/* com.tverts: support */

import com.tverts.support.EX;


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
		if(sf == null) throw EX.state();

		Session res = sf.getCurrentSession();
		if(res == null) throw EX.state(
		  "Spring @Transaction context was not opened! No Session!");
		return res;
	}


	/* public static: basic tasks support */

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

		try
		{
			return session.createQuery(s.toString());
		}
		catch(Throwable e)
		{
			throw EX.wrap(e, "Error occurred while building Query: [", s, "]!");
		}
	}

	/**
	 * Returns the actual class of the instance.
	 */
	public static Class   type(Object obj)
	{
		return (obj == null)?(null):
		  HiberSystem.getInstance().findActualClass(obj);
	}


	/* public static: keys generation support */

	public static void    setPrimaryKey
	  (Session session, NumericIdentity instance)
	{
		HiberSystem.getInstance().
		  createPrimaryKey(session, instance, false);
	}

	public static  void   setPrimaryKey
	  (Session session, NumericIdentity instance, boolean fortest)
	{
		HiberSystem.getInstance().
		  createPrimaryKey(session, instance, fortest);
	}

	public static void    setPrimaryKey
	  (Tx tx, NumericIdentity instance, boolean fortest)
	{
		HiberSystem.getInstance().createPrimaryKey(
		  tx.getSessionFactory().getCurrentSession(), instance, fortest);
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