package com.tverts.actions;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.OU;


/**
 * TODO comment ActionBase
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBase implements Action
{
	/* public: Action interface (execution phases) */

	public Action clone(ActionTask task)
	{
		ActionBase res;

		//~: do standard clone
		try
		{
			res = (ActionBase)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}

		//~: cleanup the fields
		cloneCleanup(res);

		return res;
	}

	public void   bind(ActionContext context)
	{
		this.context = context;
	}

	public void   open()
	{
		openValidate();
	}

	public void   trigger()
	{
		logTriggerStarted();

		Throwable error = null;

		try
		{
			//!: invoke action
			execute();
		}
		catch(Throwable e)
		{
			error = e;
		}

		logTriggerDone(error);

		//?: {has no error}
		if(error == null) return;

		//?: {has an action error} rethrow it
		if(error instanceof ActionError)
			throw (ActionError)error;

		//!: throw the error wrapped-in
		throw new ActionError(error).
		  setAction(this).setCritical(true).
		  setPhase(ActionPhase.TRIGGER);
	}

	public void   close()
	{}

	/* public: Action interface (the state access) */

	public ActionTask    getTask()
	{
		return task;
	}

	public ActionContext getContext()
	{
		return context;
	}

	public Map           getFeatures()
	{
		return (this.features != null)?(this.features):
		  (this.features = createFeatures());
	}

	public ActionError   getError()
	{
		return this.error;
	}

	/* protected: action trigger phase */

	protected abstract void execute()
	  throws Throwable;

	/* protected: action state & execution support */

	protected void openValidate()
	{
		//?: {has no action task}
		if(getTask() == null)
			throw new IllegalStateException(String.format(
			  "Open validation of %s: %s", logsig(),
			  "action has the task action reference undefined!"
		));

		//?: {has no action context}
		if(getContext() == null)
			throw new IllegalStateException(String.format(
			  "Open validation of %s: %s", logsig(),
			  "action has no action context bound!"
		));
	}

	protected Map  createFeatures()
	{
		return new HashMap(1);
	}

	protected void setError(ActionError error)
	{
		this.error = error;
	}

	protected void cloneCleanup(ActionBase clone)
	{
		clone.task     = null;
		clone.context  = null;
		clone.features = null;
		clone.error    = null;
	}

	/* protected: logging */

	protected String getLog()
	{
		return ActionsPoint.LOG_ACTION;
	}

	protected void   logTriggerStarted()
	{
		if(LU.isD(getLog())) LU.D(getLog(),
		  "started trigger of ", logsig()
		);
	}

	protected void   logTriggerDone(Throwable error)
	{
		//?: {has no error}
		if(LU.isD(getLog()) && (error == null))
			LU.D(getLog(), "successfully triggered ", logsig());

		//?: {not critical action error}
		if((error instanceof ActionError) &&
		   !((ActionError)error).isCritical()
		  )
		{
			LU.E(getLog(), "noncritical failure on trigger ", logsig());
			return;
		}

		//?: {other error}
		if(error != null)
			LU.E(getLog(), "failed trigger ", logsig());
	}

	protected String logsig()
	{
		return "action " + OU.sig(this);
	}

	protected String logsig(ActionTask task)
	{
		return ActionsPoint.logsig(task);
	}

	protected String logsig(ActionContext context)
	{
		return ActionsPoint.logsig(context);
	}

	/* private: action state */

	private ActionTask    task;
	private ActionContext context;
	private Map           features;
	private ActionError   error;
}