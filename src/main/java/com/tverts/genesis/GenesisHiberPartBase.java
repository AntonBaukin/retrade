package com.tverts.genesis;

/* standard Java classes */

import java.util.Date;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: support */

import static com.tverts.support.DU.date2str;
import com.tverts.support.LU;


/**
 * A part of {@link GenesisSphere} that is invoked in
 * a transaction context.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class GenesisHiberPartBase
       extends        GenesisPartBase
{
	/* public: Genesis interface */

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
	 * {@link TxPoint#txSession()}.
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
		return (sessionFactory == null)?(TxPoint.txSession()):
		  (sessionFactory.getCurrentSession());
	}

	protected Query        Q(String hql)
	{
		return session().createQuery(hql);
	}


	/* protected: days generation support */

	/**
	 * This implementation checks on the first call
	 * whether there are objects of the type generated
	 * are already present at the inquired day.
	 */
	protected boolean      isGenDispDayClear(GenCtx ctx, UnityType ut)
	{
		//~: create the key
		String key = createGenDispDayKey(ctx, ut);

		//?: {cached the result in the gen context}
		if(ctx.get(key) != null)
			return Boolean.TRUE.equals(ctx.get(key));

		//~: access generation property
		GetProps g = bean(GetProps.class);
		Property p = g.goc(ctx.get(Domain.class), "Genesis", key);

		//?: {there was generation for this day}
		if(g.bool(p) != null)
		{
			LU.I(log(ctx), logsig(), " day [",
			  ctx.get(DaysGenDisp.TIME, Date.class),
			  "] marked as generated for entities with [", ut, ']');

			ctx.set(key, false); //<-- cache the result
			return false;
		}

		ctx.set(key, true);  //<-- cache the result
		g.set(p, true);      //<-- set the property

		return true;
	}

	protected String       createGenDispDayKey(GenCtx ctx, UnityType ut)
	{
		return String.format(
		  "%s: day [%s]; type [%s]",

		  ut.getClass().getSimpleName(),
		  date2str(ctx.get(DaysGenDisp.DAY, Date.class)),
		  ut.getTypeName()
		);
	}

	/**
	 * Marks the day as it were the generation.
	 */
	protected void         markGenDispDay(GenCtx ctx, UnityType ut)
	{
		//~: create the key
		String key = createGenDispDayKey(ctx, ut);

		//~: access generation property
		GetProps g = bean(GetProps.class);
		Property p = g.goc(ctx.get(Domain.class), "Genesis", key);

		ctx.set(key, true);  //<-- cache the result
		g.set(p, true);      //<-- set the property
	}


	/* private: database access */

	private SessionFactory    sessionFactory;
	private transient Session session;
}