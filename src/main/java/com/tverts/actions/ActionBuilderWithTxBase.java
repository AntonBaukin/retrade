package com.tverts.actions;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxContext;
import com.tverts.system.tx.TxPoint;


/**
 * Most of the action builders have to access database.
 * (A few does access else transactional resources.)
 *
 * This class extends basic builder to provide this access.
 * Note that builder always works in the context of the
 * action task' transaction or the global one.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderWithTxBase
       extends        ActionBuilderBase
{
	/* protected: access transaction context */

	/**
	 * Returns the Action Tx context when it is provided.
	 * Undefined result is possible, but rare.
	 */
	protected ActionTx  getActionTx(ActionBuildRec abr)
	{
		return abr.getTask().getTx();
	}

	/**
	 * Returns the effectife transaction context.
	 * This is the action context if it is defined,
	 * or the default one otherwise.
	 *
	 * Note that it is possible the context to be undefined.
	 * It is when there is no global context.
	 */
	protected TxContext getTxOrNull(ActionBuildRec abr)
	{
		TxContext tx = getActionTx(abr);
		return (tx != null)?(tx):(getDefaultTx(abr));
	}

	/**
	 * Returns the existing {@link #getTxOrNull(ActionBuildRec)},
	 * or raises {@link IllegalStateException}.
	 */
	protected TxContext tx(ActionBuildRec abr)
	{
		TxContext tx = getTxOrNull(abr);

		if(tx == null) throw new IllegalStateException(
		  "Action Builder got a task without transaction context, " +
		  "and the default context was not found.");

		return tx;
	}

	protected TxContext getDefaultTx(ActionBuildRec abr)
	{
		return TxPoint.getInstance().getTxContext();
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session   session(ActionBuildRec abr)
	{
		SessionFactory f = tx(abr).getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "Action Builder got transaction context having " +
		  "undefined Hibernate session (or factroy)!");

		return s;
	}
}