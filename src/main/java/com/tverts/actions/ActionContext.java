package com.tverts.actions;

/* standard Java class */

import java.util.Map;


/**
 * COMMENT ActionContext
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
	public ActionTask  getTask();

	public ActionChain getChain();

	/**
	 * The Action Transaction Context shared between all the
	 * action of the chain. Note that an action still may has
	 * it's own context. The shared context may also be undefined.
	 */
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