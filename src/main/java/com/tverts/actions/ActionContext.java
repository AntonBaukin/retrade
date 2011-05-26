package com.tverts.actions;

/* standard Java class */

import java.util.Map;


/**
 * TODO comment ActionContext
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionContext
{
	/* public: ActionContext interface */

	/**
	 * Defines the action that had caused this context
	 * (the actions execution chain).
	 */
	public ActionTask  getActionTask();

	public ActionChain getActionChain();

	public ActionTx    getActionTx();

	/**
	 * Stores the variables shared between the actions.
	 * Used to establish data communication between
	 * the actions in the chain.
	 */
	public Map         getContext();

	public ActionError getError();

	public void        setError(ActionError error);
}