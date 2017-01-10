package com.tverts.genesis;

/* standard Java classes */

import java.util.Date;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;
import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: support */

import com.tverts.support.DU;
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
	/* protected: access Hibernate session */

	/**
	 * TODO implement as session(GenCtx)
	 */
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
			LU.I(log(ctx), logsig(), " skip day [",
			  DU.date2str(ctx.get(DaysGenDisp.TIME, Date.class)),
			  "] as it's marked as generated"
			);

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
		  DU.date2str(ctx.get(DaysGenDisp.DAY, Date.class)),
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


	/* protected: batched operation */

	protected void    nestTx(final GenCtx ctx, final Runnable run)
	{
		//~: remember the external session
		Session   session = ctx.session();
		Throwable error   = null;

		try
		{
			//!: run the operation in a new transaction
			bean(TxBean.class).setNew().execute(new Runnable()
			{
				public void run()
				{
					//~: bind the session to the context
					if(ctx instanceof GenCtxBase)
						((GenCtxBase)ctx).setSession(TxPoint.txSession());

					run.run();
				}
			});
		}
		catch(Throwable e)
		{
			error = e;
		}
		finally
		{
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