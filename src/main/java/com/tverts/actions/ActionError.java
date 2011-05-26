package com.tverts.actions;

/**
 * TODO comment ActionError
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

	private ActionPhase phase;
	private Action      action;
	private boolean     critical = true;
}