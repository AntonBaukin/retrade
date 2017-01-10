package com.tverts.event;

/* Hibernate Persistence Layer */

import org.hibernate.query.Query;
import org.hibernate.Session;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionWithTxBase;


/**
 * Implementation base for active event.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActiveEventBase
       extends        EventBase
       implements     ActiveEvent
{
	/* public: constructor */

	protected ActiveEventBase(Object target)
	{
		super(target);
	}


	/* public: ActiveEvent interface */

	public void    actBefore(ActionWithTxBase action)
	{
		this.action = action;
		this.actBefore();
	}

	public void    actAfter(Throwable error)
	{
		if(error instanceof RuntimeException)
			throw (RuntimeException) error;
		else if(error != null)
			throw new RuntimeException(error);
		else
			actAfter();
	}


	/* protected: action execution */

	protected void actBefore()
	{}

	protected void actAfter()
	{}

	protected ActionWithTxBase action()
	{
		return action;
	}


	/* protected: queries support */

	protected Session session()
	{
		return TxPoint.txSession(action().tx());
	}

	protected Query   Q(String hql, Object... replaces)
	{
		return HiberPoint.query(session(), hql, replaces);
	}


	/* private: the action task */

	private ActionWithTxBase action;
}