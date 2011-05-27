package com.tverts.actions;

/* standard Java class */

import java.util.Map;

/**
 * TODO comment ActionTask
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionTask
{
	/* public: ActionTask interface */

	public ActionType     getActionType();

	public Object         getTarget();

	/**
	 * Optional user-defined callback on the
	 * action processing phases.
	 */
	public ActionCallback getCallback();

	/**
	 * Additional parameters related to the environment
	 * of the action creation.
	 */
	public Map            getParams();

	/**
	 * Defines the transactional context to execute the action.
	 * If not defined, the present invocation context is selected.
	 */
	public ActionTx       getTx();
}