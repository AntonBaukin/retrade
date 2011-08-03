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
	 * Callback to invoke Action Build System on
	 * the nested task.
	 *
	 * This callback must be always defined.
	 */
	public static interface NestedBuilder
	{
		/* public: NestedBuilder interface */

		public ActionBuildRec nestAction(ActionBuildRec abr, ActionTaskNested task);
	}

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


	/* public: action build state access */

	/**
	 * Tells whether the action building is complete.
	 * If the flag is set no further attempts to update
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
		this.context = context;

		//?: {this build record is nested} propagate the context up
		if((getOuterBuildRec() != null) && (getOuterBuildRec().getContext() == null))
			getOuterBuildRec().setContext( context);

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
	 * Returns the task that had caused this actions build.
	 * This is the task provided by the outer user, or the
	 * nested task requested from some builder via the nesting.
	 */
	public ActionTask     getInitialTask()
	{
		return initialTask;
	}

	public ActionBuildRec setInitialTask(ActionTask initialTask)
	{
		this.initialTask = initialTask;
		return this;
	}

	/**
	 * If this build record is of a nested action build request,
	 * the outer action build record is saved here.
	 */
	public ActionBuildRec getOuterBuildRec()
	{
		return outerBuildRec;
	}

	public ActionBuildRec setOuterBuildRec(ActionBuildRec abr)
	{
		this.outerBuildRec = abr;
		return this;
	}

	/**
	 * There are composite builders exist that do not build
	 * actions on their own, but delegate this issue to the
	 * aggregated ones. Build step allows such a builders
	 * to make several loops of the sub-builders calling
	 * providing this distinct marker objects.
	 *
	 * @see {@link ActionBuildersRoot}.
	 */
	public Object         getBuildStep()
	{
		return buildStep;
	}

	public ActionBuildRec setBuildStep(Object buildStep)
	{
		this.buildStep = buildStep;
		return this;
	}

	/* public: action build sub-strategies access */

	/**
	 * Action system strategy callback to build nested action tasks.
	 * It is always specified by the action system components.
	 */
	public NestedBuilder  getNestedBuilder()
	{
		return nestedBuilder;
	}

	public ActionBuildRec setNestedBuilder(NestedBuilder nestedBuilder)
	{
		this.nestedBuilder = nestedBuilder;
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

	public ActionBuildRec clone(ActionTask task)
	{
		ActionBuildRec copy = new ActionBuildRec(task);

		copy.context = this.getContext();
		copy.trigger = this.getTrigger();

		copy.nestedBuilder  = this.getNestedBuilder();
		copy.contextCreator = this.getContextCreator();
		copy.triggerCreator = this.getTriggerCreator();

		return copy;
	}


	/* private: action build state */

	private ActionTask     initialTask;
	private ActionContext  context;
	private ActionTrigger  trigger;
	private ActionBuildRec outerBuildRec;
	private Object         buildStep;
	private boolean        complete;


	/* private: action build sub-strategies */

	private NestedBuilder  nestedBuilder;
	private ContextCreator contextCreator;
	private TriggerCreator triggerCreator;
}