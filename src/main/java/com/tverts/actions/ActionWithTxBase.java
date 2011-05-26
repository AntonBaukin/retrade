package com.tverts.actions;

/* Hibernate Persistence Layer */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Provides access to Action transaction context
 * and Hibernate session for this factory.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionWithTxBase extends ActionBase
{
	/* public: ActionWithTxBase interface */

	/**
	 * Returns the effective transaction
	 * context of the action.
	 */
	public ActionTx getActionTx()
	{
		return (getActionOwnTx() != null)?(getActionOwnTx()):
		  (getContext() != null)?(getContext().getActionTx()):(null);
	}

	/**
	 * Returns the overwritten transaction
	 * context of the action.
	 *
	 * The default context is stored in the
	 * action context bound.
	 */
	public ActionTx getActionOwnTx()
	{
		return actionTx;
	}

	public void     setActionOwnTx(ActionTx actionTx)
	{
		this.actionTx = actionTx;
	}

	/* protected: access transaction context */

	/**
	 * Returns the action tx, or raises
	 * {@link IllegalStateException}.
	 */
	protected ActionTx tx()
	{
		ActionTx tx = getActionTx();

		if(tx == null) throw new IllegalStateException(
		  "action has no effective transaction context");

		return tx;
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session  session()
	{
		SessionFactory f = tx().getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "action got undefined Hibernate session (factroy)");

		return s;
	}

	/* private: session factory reference */

	private ActionTx actionTx;
}