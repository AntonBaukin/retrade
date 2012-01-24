package com.tverts.actions;

/**
 * COMMENT ActionError
 *
 * @author anton.baukin@gmail.com
 */
public class ActionError extends RuntimeException
{
	/* public: constructors */

	public ActionError()
	{}

	public ActionError(Throwable cause)
	{
		super(cause);
	}

	public ActionError(String message)
	{
		super(message);
	}

	public ActionError(String message, Throwable cause)
	{
		super(message, cause);
	}

	/* public: ActionError interface */

	public ActionPhase getPhase()
	{
		return phase;
	}

	public ActionError setPhase(ActionPhase phase)
	{
		this.phase = phase;
		return this;
	}

	public Class       getActionClass()
	{
		return actionClass;
	}

	/**
	 * Returns the action that had caused this error.
	 * May be not defined if the action is unknown.
	 */
	public Action      getAction()
	{
		return action;
	}

	public ActionError setAction(Action action)
	{
		this.action = action;
		if(action != null)
			this.actionClass = action.getClass();
		return this;
	}

	public boolean     isCritical()
	{
		return critical;
	}

	public ActionError setCritical(boolean critical)
	{
		this.critical = critical;
		return this;
	}

	/* private: error state */

	private ActionPhase      phase;
	private Class            actionClass;
	private transient Action action;
	private boolean          critical = true;
}