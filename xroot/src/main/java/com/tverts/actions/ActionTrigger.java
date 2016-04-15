package com.tverts.actions;

/**
 * Envelopes {@link Action} chain execution into
 * {@link Runnable} task.
 *
 * Clients of Execution Layer deal with the instance
 * of this execution strategy obtained via global
 * execution point {@link ActionsPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActionTrigger extends Runnable
{
	/* public: ActionTrigger interface */

	public ActionContext getActionContext();

	public Boolean       isSuccess();

	public boolean       isTransactional();
}