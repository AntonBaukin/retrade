package com.tverts.actions;

/* standard Java classes */

import java.util.Map;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;


/**
 * Action of the System Execution Layer.
 *
 * Actions are never invoked on their own. They form
 * a chain, and invocation is done in the defined order.
 *
 * Actions are stateful. It is allowed to store execution
 * state between the phases in the action instance.
 *
 * There are fours execution phases. Bind associates the
 * action with the execution context {@link ActionContext}.
 * Open phase is the initialization, trigger phase does
 * the actual work, and close phase cleanups the results.
 *
 * COMMENT continue comments on Action
 *
 * @author anton.baukin@gmail.com
 */
public interface Action
{
	/* Action (execution phases) */

	public void   bind(ActionContext context);

	public void   open();

	public void   trigger();

	/**
	 * Cleanups the results of the execution.
	 *
	 * Invoked even if the trigger phase fails,
	 * but open phase was successful.
	 */
	public void   close();


	/* Action (the state access) */

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
	 * regarded as critical exception that stops
	 * the whole execution.
	 */
	public ActionError   getError();

	/**
	 * Each action may have the predicate controlling
	 * whether to execute it or not. As the parameter
	 * predicate receives this action instance.
	 *
	 * If a predicate is not set, or is set and returns
	 * {@code true}, the action is executed.
	 *
	 * Note that predicate controls only the trigger
	 * phase of the action, not the open or the close ones.
	 * Use {@link ActionBase#isPredicate()} to check
	 * the execution status on the close phase.
	 *
	 * Important that is not safe to call the predicate
	 * on the open phase. Predicate may rely on the data
	 * created in trigger runs of the actions previous
	 * to this action in the chain. Be careful here!
	 */
	public Predicate     getPredicate();

	public Action        setPredicate(Predicate p);
}