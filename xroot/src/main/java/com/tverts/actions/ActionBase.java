package com.tverts.actions;

/* Java */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;

/* com.tverts: predicates */

import com.tverts.support.logic.Predicate;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Implementation base for an execution layer Action.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBase implements Action
{
	/* public: constructor */

	public ActionBase(ActionTask task)
	{
		this.task = EX.assertn(task);
	}


	/* public: Action interface (execution phases) */

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
			long    td = System.currentTimeMillis();
			boolean ok = isPredicate();

			if(System.currentTimeMillis() - td > 150L) LU.D(LU.LOGT,
			  getClass().getSimpleName(), " evaluation of predicate ",
			  (getPredicate() == null)?(""):(getPredicate().getClass().getSimpleName()),
			  " took ", LU.td(td), '!');

			//?: {predicate allows} invoke the action
			if(ok)
			{
				td = System.currentTimeMillis();
				execute();

				if(System.currentTimeMillis() - td > 250L) LU.D(LU.LOGT,
				  getClass().getSimpleName(), " execution took ", LU.td(td), '!');
			}
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

	public Predicate     getPredicate()
	{
		return predicate;
	}

	public ActionBase    setPredicate(Predicate predicate)
	{
		this.predicate = predicate;
		return this;
	}


	/* protected: action trigger phase */

	protected abstract void execute()
	  throws Throwable;

	/**
	 * Tells whether {@link #execute()} method
	 * must be actually invoked. May be invoked
	 * in each phase of action processing.
	 *
	 * By default it affects the execution phase only.
	 */
	protected boolean       isPredicate()
	{
		return (executing != null)?(executing):(executing = (
		  (getPredicate() == null) || getPredicate().evalPredicate(this)));
	}


	/* protected: action state & execution support */

	protected void openValidate()
	{
		//?: {has no action task}
		EX.assertn(getTask(), "Open validation of ", logsig(),
		  ": action has the task object reference undefined!");

		//?: {has no action context}
		EX.assertn(getContext(), "Open validation of ", logsig(),
		  ": action has no action context bound!");
	}

	protected Map  createFeatures()
	{
		return new HashMap(1);
	}

	protected void setError(ActionError error)
	{
		this.error = error;
	}


	/* protected: action execution helpers */

	protected Object  targetOrNull()
	{
		return getTask().getTarget();
	}

	@SuppressWarnings("unchecked")
	protected <O> O   targetOrNull(Class<O> tclass)
	{
		Object res = getTask().getTarget();

		//?: {the target is not defined}
		if(res == null) return null;

		//?: {has wrong target class}
		if((tclass != null) && !tclass.isAssignableFrom(res.getClass()))
			throw EX.state("Action target [", LU.sig(res),
			  "] can't be cast to class [", tclass.getName(), "]!");

		return (O)getTask().getTarget();
	}

	protected Object  target()
	{
		return EX.assertn(getTask().getTarget(),
		  "Action requires the target instance that is undefined!"
		);
	}

	/**
	 * While {@link #target()} returns the actual object
	 * stored in the action task, this call also checks
	 * whether the target is wrapped or created with
	 * a delay. It returns the object meant to be a target.
	 */
	protected Object  xtarget()
	{
		Object target = target();

		if(target instanceof DelayedInstance)
			target = ((DelayedInstance)target).createInstance(this);

		return EX.assertn(target,
		  "Action requires the target instance that is undefined!"
		);
	}

	@SuppressWarnings("unchecked")
	protected <O> O   target(Class<O> tclass)
	{
		Object target = target();

		//?: {has wrong target class}
		if((tclass != null) && !tclass.isAssignableFrom(target.getClass()))
			throw EX.state("Action target [", LU.sig(target),
			  "] can't be cast to class [", tclass.getName(), "]!");

		return (O)target;
	}

	@SuppressWarnings("unchecked")
	protected <O> O   xtarget(Class<O> tclass)
	{
		Object target = xtarget();

		//?: {has wrong target class}
		if((tclass != null) && !tclass.isAssignableFrom(target.getClass()))
			throw EX.state("Action target [", LU.sig(target),
			  "] can't be cast to class [", tclass.getName(), "]!");

		return (O)target;
	}

	protected boolean isTestTarget()
	{
		Object target = target();

		return (target instanceof NumericIdentity) &&
		  isTestInstance((NumericIdentity)target);
	}

	protected Object  param(Object name)
	{
		return getTask().getParams().get(name);
	}

	@SuppressWarnings("unchecked")
	protected <T> T   param(Object name, Class<T> pclass)
	{
		Object res = getTask().getParams().get(name);

		if((res != null) && (pclass != null) && !pclass.isAssignableFrom(res.getClass()))
			throw EX.state("Action asked parameter '",
			  (name == null)?("UNDEFINED"):(name), "' as of the class [",
			  pclass.getName(), "], but actual is [", res.getClass().getName(), "]!"
			);

		return (T)res;
	}

	protected boolean flag(Object name)
	{
		return Boolean.TRUE.equals(param(name));
	}


	/* protected: logging */

	protected String getLog()
	{
		return LU.getLogBased(Action.class, this);
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
		return "action " + LU.sig(this);
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
	private Predicate     predicate;
	private Boolean       executing;
}