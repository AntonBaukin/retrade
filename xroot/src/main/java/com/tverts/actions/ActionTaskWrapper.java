package com.tverts.actions;

import java.util.Map;

/**
 * Wrapper of an {@link ActionTask}.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionTaskWrapper implements ActionTask
{
	/* public: constructor */

	public ActionTaskWrapper(ActionTask task)
	{
		if(task == null) throw new IllegalArgumentException();
		this.task = task;
	}

	/* public: ActionTask interface */

	public ActionType     getActionType()
	{
		return task.getActionType();
	}

	public Object         getTarget()
	{
		return task.getTarget();
	}

	public ActionCallback getCallback()
	{
		return task.getCallback();
	}

	public Map            getParams()
	{
		return task.getParams();
	}

	public ActionTx       getTx()
	{
		return task.getTx();
	}

	/* protected: the task reference */

	protected final ActionTask task;
}