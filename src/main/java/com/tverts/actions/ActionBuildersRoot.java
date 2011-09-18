package com.tverts.actions;

/* standard Java classes */

import java.util.List;


/**
 * Does not build actions by itself. It aggregates
 * a reference to actions and polls them.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionBuildersRoot extends ActionBuilderSystem
{
	/* build steps */

	/**
	 * Root builder does two loops of polling
	 * the aggregated builders. This instance
	 * is the first' step marker.
	 *
	 * In the first poll loop the dispatchers
	 * {@link ActionBuilderTypeChecker} must check
	 * only that the assigned class matches
	 * the class of {@link ActionType}.
	 *
	 * @see {@link ActionBuildRec#getBuildStep()}
	 */
	public static final Object STEP_TASK   =
	  new Object();

	/**
	 * Root builder does two loops of polling
	 * the aggregated builders. This instance
	 * is the seconds' step marker.
	 *
	 * In the second poll loop the dispatchers
	 * {@link ActionBuilderTypeChecker} must check
	 * only that the assigned class matches
	 * the class of the target.
	 *
	 * @see {@link ActionBuildRec#getBuildStep()}
	 */
	public static final Object STEP_TARGET =
	  new Object();

	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		//~: check the action task
		checkActionBuildRec(abr);

		//~: initialize the system callbacks
		init(abr);

		//?: {no aggregated | the record is complete}
		if((getReference() == null) || complete(abr))
			return;

		//!: do poll the builders
		poll(abr);
	}


	/* public: ActionBuildersRoot bean interface */

	public ActionBuilderReference getReference()
	{
		return reference;
	}

	public void setReference(ActionBuilderReference reference)
	{
		this.reference = reference;
	}


	/* protected: builders polling */

	protected void poll(ActionBuildRec abr)
	{
		List<ActionBuilder> abs = getReference().dereferObjects();

		//~: poll the task step
		poll(abs, abr, STEP_TASK);

		//?: {the build is completed} skip second step
		if(complete(abr)) return;

		//~: poll the target step
		poll(abs, abr, STEP_TARGET);
	}

	protected void poll(List<ActionBuilder> abs, ActionBuildRec abr, Object step)
	{
		//~: set the step
		Object ostep = abr.getBuildStep();
		abr.setBuildStep(step);

		//~: poll the aggregated builders
		for(ActionBuilder ab : abs)
		{
			//!: delegate the building
			ab.buildAction(abr);

			//?: {the action build is complete} quit
			if(complete(abr))
			{
				abr.setBuildStep(ostep);
				return;
			}
		}

		abr.setBuildStep(ostep);
	}

	/* private: action builders reference */

	private ActionBuilderReference reference;
}