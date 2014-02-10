package com.tverts.actions;

/* standard Java classes */

import java.util.IdentityHashMap;
import java.util.Map;

/* com.tverts: actions */

import static com.tverts.actions.ActionPhase.BIND;
import static com.tverts.actions.ActionPhase.CLOSE;
import static com.tverts.actions.ActionPhase.OPEN;
import static com.tverts.actions.ActionPhase.TRIGGER;

/* com.tverts: transactions */

import com.tverts.support.EX;
import com.tverts.system.tx.TxPoint;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Straight implementation of actions chain invocation.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionsRunner implements ActionTrigger
{
	/* public: constructor */

	public ActionsRunner(ActionContext context)
	{
		this.context = EX.assertn(context);
	}


	/* public: ActionTrigger interface */

	public ActionContext getActionContext()
	{
		return this.context;
	}

	public Boolean       isSuccess()
	{
		return this.success;
	}

	public boolean       isTransactional()
	{
		return true;
	}


	/* public: Runnable interface */

	public void          run()
	{
		try
		{
			doRun();
		}
		catch(Throwable e)
		{
			if(!(e instanceof ActionError))
				//~: can't define the phase and impulse here
				e = new ActionError(e).setCritical(true);

			//?: {has the area without error}
			if(getActionContext().getError() == null)
				getActionContext().setError((ActionError)e);

			setSuccess(false);

			//!: raise the unhandled error out
			throw (ActionError)e;
		}

		//?: {has handled critical error} not throw it out
		setSuccess(getActionContext().getError() == null);
	}


	/* protected: trigger execution */

	protected void       doRun()
	  throws Throwable
	{
		ActionPhase phase = BIND;

		logRunStart();

		try
		{
			//0: bind context phase
			phaseBindContext();

			//1: initialization phase
			phase = OPEN;
			phaseOpenActions();

			//2: trigger phase
			phase = TRIGGER;
			phaseTriggerActions();

			//3: close phase
			phase = CLOSE;
			phaseCloseActions();
		}
		catch(Throwable e)
		{
			//!: handle the error
			handleRunError(phase, e);
		}

		logRunDone();
	}

	protected void       phaseBindContext()
	  throws Throwable
	{
		for(Action action : getActionContext().getChain())
			bindAction(action);
	}

	@SuppressWarnings("unchecked")
	protected void       phaseOpenActions()
	  throws Throwable
	{
		for(Action action : getActionContext().getChain())
			getOpenedActions().put(
			  action, openAction(action)
			);
	}

	protected void       phaseTriggerActions()
	  throws Throwable
	{
		for(Action action : getActionContext().getChain())
			triggerAction(action, getOpenedActions().get(action));
	}

	protected void       phaseCloseActions()
	  throws Throwable
	{
		for(Action action : getActionContext().getChain())
			//?: {has this action opened} close it
			if(getOpenedActions().containsKey(action))
			{
				closeAction(action, getOpenedActions().get(action));
			}

		//~: flush the session
		TxPoint.txSession(getActionContext().getActionTx()).flush();
	}

	protected void       bindAction(Action action)
	  throws Throwable
	{
		callback(action, ActionPhase.BIND, true, null);
		action.bind(getActionContext());
		callback(action, ActionPhase.BIND, false, null);
	}

	/**
	 * Opens the action and returns the internal structure
	 * instance associated with this operation.
	 */
	protected Object     openAction(Action action)
	  throws Throwable
	{
		Object opener = null;

		try
		{
			opener = createOpener(action);

			callback(action, ActionPhase.OPEN, true, opener);
			action.open();
			callback(action, ActionPhase.OPEN, false, opener);
		}
		catch(ActionError e)
		{
			LU.E(getLog(), e, "Noncritical error occured while opening ",
			  logsig(action), "...");

			//?: {this error is critical} rethrow it
			if(e.isCritical())
				throw e;
		}

		return opener;
	}

	protected void       triggerAction(Action action, Object opener)
	  throws Throwable
	{
		//?: {has illegal opener instance}
		if(!isOpenerOpened(opener))
			throw new ActionError("action is not opened").
			  setAction(action).
			  setCritical(true).
			  setPhase(ActionPhase.TRIGGER);

		try
		{
			callback(action, ActionPhase.TRIGGER, true, opener);
			action.trigger();
			callback(action, ActionPhase.TRIGGER, false, opener);
		}
		catch(ActionError e)
		{
			LU.E(getLog(), e, "Noncritical error occured while triggering ",
			  logsig(action), "...");

			//?: {this error is critical} rethrow it
			if(e.isCritical())
				throw e;
		}
	}

	/**
	 * Closes the action given with the support of action open
	 * structure instance. Note that that instance is not removed
	 * from {@link #getOpenedActions()} even if this call
	 */
	@SuppressWarnings("unchecked")
	protected void       closeAction(Action action, Object opener)
	  throws Throwable
	{
		//?: {wrong open structure instance} exit now
		if(!isOpenerOpened(opener)) return;
		getOpenedActions().put(action, closeOpener(opener));

		try
		{
			callback(action, ActionPhase.CLOSE, true, opener);
			action.close();
			callback(action, ActionPhase.CLOSE, true, opener);
		}
		catch(Throwable e)
		{
			handleCloseError(action, e);
		}
	}

	protected void       callback
	  (Action action, ActionPhase phase, boolean before, Object opener)
	{
		ActionCallback ac = action.getTask().getCallback();
		if(ac == null) return;

		//!: invoke the callback
		ac.actionCallback(action, phase, before);
	}

	protected void       handleRunError(ActionPhase phase, Throwable error)
	  throws Throwable
	{
		EX.assertn(error);

		LU.E(getLog(), error, "Error occured while handling phase [",
		  phase, "] of actions chain execution! With ", logsig(getActionContext()), "!"
		);

		//?: {open | trigger phase} close the actions opened
		if(OPEN.equals(phase) || TRIGGER.equals(phase)) try
		{
			//!: close ignoring possible errors
			phaseCloseActions();
		}
		catch(Throwable e)
		{
			//~: ignore  this error
		}

		//!: default behaviour for bind and close phases
		throw error;
	}

	/**
	 * Handles the action close error. Note that this method
	 * may not throw out any (runtime) exception: the latter
	 * would be ignored.
	 */
	protected void       handleCloseError(Action action, Throwable error)
	{
		if((error instanceof ActionError) &&
		   !((ActionError)error).isCritical()
		  )
			LU.E(getLog(), error,
			  "Noncritical error occured while closing ", logsig(action), "...");
		else
			LU.E(getLog(), error,
			  "Error occured while closing ", logsig(action), "!");
	}

	protected void       setSuccess(Boolean success)
	{
		this.success = success;
	}

	/**
	 * Maps the opened action to some internal structure
	 * instance created by {@link #openAction(Action)}.
	 *
	 * Undefined results are allowed. In base implementation
	 * the result is {@link Boolean#TRUE}.
	 */
	protected Map        getOpenedActions()
	{
		return (opened != null)?(opened):
		  (opened = createOpenedActionsMap());
	}

	/**
	 * Creates an empty identity mapping of the actions.
	 */
	protected Map        createOpenedActionsMap()
	{
		return new IdentityHashMap(
		  getActionContext().getChain().size());
	}

	protected Object     createOpener(Action action)
	{
		return Boolean.TRUE;
	}

	protected boolean    isOpenerOpened(Object opener)
	{
		return Boolean.TRUE.equals(opener);
	}

	protected Object     closeOpener(Object opener)
	{
		return Boolean.FALSE;
	}


	/* protected: logging */

	protected String getLog()
	{
		return LU.getLogBased(Action.class, this);
	}

	protected void   logRunStart()
	{
		if(LU.isD(getLog())) LU.D(getLog(),
		  "started ", logsig(), " with ",
		  logsig(getActionContext().getChain())
		);
	}

	protected void   logRunDone()
	{
		if(LU.isD(getLog())) LU.D(getLog(),
		  "successfully done ", logsig()
		);
	}

	protected String logsig()
	{
		return "action trigger " + LU.sig(this);
	}

	protected String logsig(Action action)
	{
		return ActionsPoint.logsig(action);
	}

	protected String logsig(ActionTask task)
	{
		return ActionsPoint.logsig(task);
	}

	protected String logsig(ActionContext context)
	{
		return ActionsPoint.logsig(context);
	}

	protected String logsig(ActionChain chain)
	{
		return ActionsPoint.logsig(chain);
	}


	/* private: trigger state */

	private ActionContext context;
	private Boolean       success;
	private Map           opened;
}