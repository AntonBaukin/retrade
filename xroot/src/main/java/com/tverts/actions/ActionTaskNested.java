package com.tverts.actions;

/**
 * Nested Action Task comes not from an outer user
 * of Actions Subsystem, but from a builder that
 * wants to delegate extending it's own action context
 * (usually, adding actions to the chain) to another builder.
 *
 * Delegating of actions creation is a crucial benefit
 * of the Actions System implementation that allows to
 * decouple actions handling various parts of the system.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionTaskNested extends ActionTask
{
	/* public: ActionTaskNested interface */

	/**
	 * Returns the original task taht had caused this task.
	 * It is always defined.
	 */
	public ActionTask getOuterTask();
}