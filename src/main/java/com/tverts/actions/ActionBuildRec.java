package com.tverts.actions;

/**
 * Processing structure handled by the net of
 * {@link ActionBuilder} instances.
 *
 * Carries the data needed to build{@link ActionContext}
 * and {@link ActionTrigger} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionBuildRec
{
	/* public: constructor */

	public ActionBuildRec(ActionTask initialTask)
	{
		this.initialTask = initialTask;
	}

	/* public: callbacks to action system */

	/**
	 * If particular action builder does not need
	 * action context with own (extended) implementation,
	 * it creates the default one with this callback.
	 */
	public static interface ContextCreator
	{
		/* public: ContextCreator interface */

		public ActionContext createContext(ActionBuildRec abr);
	}

	/**
	 * Callback to create the action trigger with
	 * default implementation. The trigger is created
	 * only after the action context exists and is ready.
	 */
	public static interface TriggerCreator
	{
		/* public: ContextCreator interface */

		public ActionTrigger createTrigger(ActionBuildRec abr);
	}

	/* public: build data access */

	/**
	 * Tells whether the action building is complete.
	 * If the flag is set no futher attempts to update
	 * this record or the aggregated objects may be
	 * issued by the Actions Subsystem' components.
	 */
	public boolean        isComplete()
	{
		return complete;
	}

	public ActionBuildRec setComplete()
	{
		this.complete = true;
		return this;
	}

	/**
	 * The initial task that was send to actions system.
	 * It is always defined.
	 */
	public ActionTask     getTask()
	{
		return initialTask;
	}

	/**
	 * Returns the context stored in the record.
	 * The context is allowed to be rewritten.
	 */
	public ActionContext  getContext()
	{
		return context;
	}

	/**
	 * Provides the Action Context related to this build.
	 * Once the context is set it may not be changed.
	 */
	public ActionBuildRec setContext(ActionContext context)
	{
		if((this.context != null) && (this.context != context))
			throw new IllegalStateException();

		this.context = context;
		return this;
	}

	/**
	 * Action trigger is the final point invoked by the user.
	 * It is the result of the build.
	 */
	public ActionTrigger  getTrigger()
	{
		return trigger;
	}

	public ActionBuildRec setTrigger(ActionTrigger trigger)
	{
		this.trigger = trigger;
		return this;
	}

	/**
	 * Returns the action context creator strategy.
	 * It is always installed by the action system components.
	 */
	public ContextCreator getContextCreator()
	{
		return contextCreator;
	}

	public ActionBuildRec setContextCreator(ContextCreator cc)
	{
		if(cc == null) throw new IllegalArgumentException();
		this.contextCreator = cc;
		return this;
	}

	/**
	 * Returns the action trigger creator strategy.
	 * It is always installed by the action system components.
	 */
	public TriggerCreator getTriggerCreator()
	{
		return triggerCreator;
	}

	public ActionBuildRec setTriggerCreator(TriggerCreator tc)
	{
		if(tc == null) throw new IllegalArgumentException();
		this.triggerCreator = tc;
		return this;
	}

	/* private: action build state */

	private ActionTask     initialTask;
	private ActionContext  context;
	private ActionTrigger  trigger;
	private boolean        complete;

	/* private: action build substrategies */

	private ContextCreator contextCreator;
	private TriggerCreator triggerCreator;
}