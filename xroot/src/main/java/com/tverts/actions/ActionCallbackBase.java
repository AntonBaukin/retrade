package com.tverts.actions;

/**
 * Dispatches action callback invocation to the
 * internal methods that are empty by default.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionCallbackBase
       implements     ActionCallback
{
	/* public: ActionCallback interface */

	public void actionCallback(Action action, ActionPhase phase, boolean before)
	{
		//?: {before context bind}
		if(ActionPhase.BIND.equals(phase) & before)
		{
			beforeBind(action);
			return;
		}

		//?: {after context bind}
		if(ActionPhase.BIND.equals(phase) & !before)
		{
			afterBind(action);
			return;
		}

		//?: {before context opened}
		if(ActionPhase.OPEN.equals(phase) & before)
		{
			beforeOpen(action);
			return;
		}

		//?: {after context opened}
		if(ActionPhase.OPEN.equals(phase) & !before)
		{
			afterOpen(action);
			return;
		}

		//?: {before context triggered}
		if(ActionPhase.TRIGGER.equals(phase) & before)
		{
			beforeTrigger(action);
			return;
		}

		//?: {after context triggered}
		if(ActionPhase.TRIGGER.equals(phase) & !before)
		{
			triggered(action);
			return;
		}

		//?: {before context closed}
		if(ActionPhase.CLOSE.equals(phase) & before)
		{
			beforeClose(action);
			return;
		}

		//?: {after context closed}
		if(ActionPhase.CLOSE.equals(phase) & !before)
		{
			afterClose(action);
			return;
		}
	}

	/* protected: callback phases reaction */

	protected void beforeBind(Action action)
	{}

	protected void afterBind(Action action)
	{}

	protected void beforeOpen(Action action)
	{}

	protected void afterOpen(Action action)
	{}

	protected void beforeTrigger(Action action)
	{}

	protected void triggered(Action action)
	{}

	protected void beforeClose(Action action)
	{}

	protected void afterClose(Action action)
	{}
}