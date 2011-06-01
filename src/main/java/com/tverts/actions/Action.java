package com.tverts.actions;

import java.util.Map;

/**
 * Action of the System Execution Layer.
 *
 * Actions are never invoked on their own. They
 * form a chain, and invocation is done in
 * the defined order.
 *
 * Actions are stateful. It is allowed to store
 * executonal state between the phases in the
 * action instance.
 *
 * There are fours execution phases. Bind associates the
 * action with the execution context {@link ActionContext}.
 * Open phase is the initialization, trigger phase does
 * the actual work, and close phase cleanups the results.
 *
 * TODO continue comments on Action
 *
 * @author anton.baukin@gmail.com
 */
public interface Action
{
	/* public: Action interface (execution phases) */

	public void   bind(ActionContext context);

	public void   open();

	public void   trigger();

	/**
	 * Cleanups the results of the execution.
	 *
	 * Invoked even if the trigger phase fails,
	 * but open phase was successfull.
	 */
	public void   close();

	/* public: Action interface (the state access) */

	/**
	 * The task that had caused this action.
	 *
	 * Note that a task may be related with
	 * more than one action.
	 *
	 * By default all actions refer the same task
	 * that was send initially to the actions system.
	 */
	public ActionTask    getTask();

	/**
	 * If action does some result (creates and saves instances)
	 * it is returned here. The result is defined after the
	 * trigger call. The actual class of the result depends on
	 * action. It may be a composite structure.
	 */
	public Object        getResult();

	/**
	 * Returns the context bound to this task.
	 */
	public ActionContext getContext();

	/**
	 * Various properties of the action that are not exposed
	 * via the static programmatic interface.
	 */
	public Map           getFeatures();

	/**
	 * The error of {@link #trigger()} execution.
	 * If an exception leaks out of trigger, it is
	 * regarded as critical execption that stops
	 * the whole execution.
	 */
	public ActionError   getError();
}