package com.tverts.event;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
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

	public void act(ActionWithTxBase action)
	{
		this.action = action;
		this.act();
	}


	/* protected: action execution */

	protected abstract void    act();

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