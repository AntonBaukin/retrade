package com.tverts.genesis;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: hibernate */

import com.tverts.hibery.HiberPoint;


/**
 * A part of {@link GenesisSphere} that is invoked in
 * a thansactional context.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GenesisHiberPartBase
       extends        GenesisPartBase
{
	/* public: Genesis interface */

	public boolean         isTransactional()
	{
		return true;
	}

	public Genesis         clone()
	{
		GenesisHiberPartBase obj =
		  (GenesisHiberPartBase)super.clone();

		//~: clear the session (for additional protection)
		obj.session = null;
		return obj;
	}

	/* public: GenesisHiberPartBase bean interface */

	/**
	 * You may specify the Session Factory explicitly.
	 * By default the session is accessed by the call
	 * {@link HiberPoint#session()}.
	 *
	 * Once the session is obtained, it is not changed
	 * during the whole generation.
	 */
	public SessionFactory  getSessionFactory()
	{
		return sessionFactory;
	}

	public void            setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	/* protected: access Hibernate session */

	protected Session      session()
	{
		return (session != null)?(session):
		  (session = obtainSession());
	}

	protected Session      obtainSession()
	{
		return (sessionFactory == null)
		  ?(HiberPoint.session())
		  :(sessionFactory.getCurrentSession());
	}

	/* private: database access */

	private SessionFactory    sessionFactory;
	private transient Session session;
}