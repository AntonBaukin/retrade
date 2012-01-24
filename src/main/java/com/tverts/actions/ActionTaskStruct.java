package com.tverts.actions;

/* standard Java class */

import java.util.HashMap;
import java.util.Map;

/**
 * COMMENT ActionTaskStruct
 *
 * @author anton.baukin@gmail.com
 */
public class ActionTaskStruct implements ActionTask
{
	/* public: constructor */

	public ActionTaskStruct(ActionType actionType)
	{
		if(actionType == null)
			throw new IllegalArgumentException();

		this.actionType = actionType;
	}

	/* public: ActionTask interface */

	public ActionType     getActionType()
	{
		return actionType;
	}

	public Object         getTarget()
	{
		return target;
	}

	public ActionCallback getCallback()
	{
		return callback;
	}

	public Map            getParams()
	{
		return (params != null)?(params):
		  (params = new HashMap(1));
	}

	public ActionTx       getTx()
	{
		return tx;
	}

	/* public: ActionTaskStruct interface */

	public ActionTaskStruct setTarget(Object target)
	{
		this.target = target;
		return this;
	}

	public ActionTaskStruct setCallback(ActionCallback callback)
	{
		this.callback = callback;
		return this;
	}

	public ActionTaskStruct setParams(Map params)
	{
		this.params = params;
		return this;
	}

	public ActionTaskStruct setTx(ActionTx tx)
	{
		this.tx = tx;
		return this;
	}

	/* private: the task contents */

	private ActionType     actionType;
	private Object         target;
	private ActionCallback callback;
	private Map            params;
	private ActionTx       tx;
}