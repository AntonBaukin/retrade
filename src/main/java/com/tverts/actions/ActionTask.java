package com.tverts.actions;

/* standard Java class */

import java.util.Map;

/**
 * Task of action to execute.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionTask
{
	/* public: ActionTask interface */

	/**
	 * Defines the type of the action to execute.
	 * The type is always defined.
	 */
	public ActionType     getActionType();

	/**
	 * Returns the central object this task wants
	 * to be processed by the actions. Surrounding
	 * environment may be stored in {@link #getParams()}.
	 *
	 * Note that the target may be undefined.
	 */
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