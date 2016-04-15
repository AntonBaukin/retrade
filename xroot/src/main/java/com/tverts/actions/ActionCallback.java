package com.tverts.actions;

/**
 * A callback to the action processing phases.
 *
 * Collbacks are invoked only by the system
 * components, not the actions themself.
 *
 * Callbacks are provided by user in action tasks
 * to react on the action execution results.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionCallback
{
	/* public: ActionCallback interface */

	/**
	 * Callback invocation on the action phase. Parameter 'before'
	 * tells whether the call is before or after the phase. Each
	 * phase has two calls.
	 */
	public void actionCallback(Action action, ActionPhase phase, boolean before);
}