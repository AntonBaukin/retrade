package com.tverts.genesis;

/* standard Java classes */

import java.util.Date;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;

/* Spring framework */

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.tverts.support.EX;
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

	public Genesis    clone()
	{
		return (GenesisHiberPartBase) super.clone();
	}


	/* protected: access Hibernate session */

	protected Session session()
	{
		return TxPoint.txSession();
	}

	protected Query   Q(String hql)
	{
		return session().createQuery(hql);
	}


	/* protected: days generation support */

	/**
	 * This implementation checks on the first call
	 * whether there are objects of the type generated
	 * are already present at the inquired day.
	 */
	protected boolean isGenDispDayClear(GenCtx ctx, UnityType ut)
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

	protected String  createGenDispDayKey(GenCtx ctx, UnityType ut)
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
	protected void    markGenDispDay(GenCtx ctx, UnityType ut)
	{
		//~: create the key
		String key = createGenDispDayKey(ctx, ut);

		//~: access generation property
		GetProps g = bean(GetProps.class);
		Property p = g.goc(ctx.get(Domain.class), "Genesis", key);

		ctx.set(key, true);  //<-- cache the result
		g.set(p, true);      //<-- set the property
	}


	/* protected: special operations */

	@Transactional(rollbackFor = Throwable.class,
	  propagation = Propagation.REQUIRES_NEW)
	protected void    nestTx(GenCtx ctx, Runnable run)
	{
		//~: remember the external session
		Session   session = ctx.session();
		Throwable error   = null;

		//~: push default tx-context for this new transaction
		TxPoint.getInstance().setTxContext();

		try
		{
			//~: bind the session to the context
			if(ctx instanceof GenCtxBase)
				((GenCtxBase)ctx).setSession(TxPoint.txSession());

			//!: run the operation
			run.run();
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
			//!: pop transaction context
			try
			{
				TxPoint.getInstance().setTxContext(null);
			}
			catch(Throwable e)
			{
				if(error == null) error = e;
			}

			//~: restore the original session of the context
			if(ctx instanceof GenCtxBase)
				//?: {there was differ external session}
				if(session != ctx.session())
					((GenCtxBase)ctx).setSession(session);
				else
					((GenCtxBase)ctx).setSession(null);
		}

		if(error != null)
			throw EX.wrap(EX.xrt(error));
	}
}