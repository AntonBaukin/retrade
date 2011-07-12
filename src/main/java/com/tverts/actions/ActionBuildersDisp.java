package com.tverts.actions;

/* com.tverts: actions */

import static com.tverts.actions.ActionBuildersRoot.STEP_TASK;
import static com.tverts.actions.ActionBuildersRoot.STEP_TARGET;


/**
 * TODO comment ActionBuildersDisp
 *
 * @author anton.baukin@gmail.com
 */
public class ActionBuildersDisp extends ActionBuilderBase
{
	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		//?: {not that case | no builders}
		if(!isActionBuildPossible(abr) || (getReference() == null))
			return;

		//!: invoke the builders aggregated
		invokeBuilders(abr);
	}

	/* public: ActionBuildersDisp bean interface */

	public Class   getTargetClass()
	{
		return targetClass;
	}

	public void    setTargetClass(Class targetClass)
	{
		this.targetClass = targetClass;
	}

	public boolean isSubclasses()
	{
		return subclasses;
	}

	public void    setSubclasses(boolean subclasses)
	{
		this.subclasses = subclasses;
	}

	public ActionBuilderReference
	               getReference()
	{
		return reference;
	}

	public void    setReference(ActionBuilderReference reference)
	{
		this.reference = reference;
	}

	/* protected: action build dispatching */

	protected boolean isActionBuildPossible(ActionBuildRec abr)
	{
		return isActionTypeMatch(abr) || isActionTargetMatch(abr);
	}

	@SuppressWarnings("unchecked")
	protected boolean isActionTypeMatch(ActionBuildRec abr)
	{
		//?: {the target class is not defined} illegal config
		if(getTargetClass() == null)
			return false;

		//?: {this is not the task step}
		if(!isBuildStep(abr, null) && !isBuildStep(abr, STEP_TASK))
			return false;

		Class c = abr.getTask().getActionType().getRefClass();

		return isSubclasses()
		  ?(getTargetClass().isAssignableFrom(c))
		  :(getTargetClass().equals(c));
	}

	@SuppressWarnings("unchecked")
	protected boolean isActionTargetMatch(ActionBuildRec abr)
	{
		//?: {the target class is not defined} illegal config
		if(getTargetClass() == null)
			return false;

		//?: {there is no target instance}
		if(abr.getTask().getTarget() == null)
			return false;

		//?: {this is not the target step}
		if(!isBuildStep(abr, null) && !isBuildStep(abr, STEP_TARGET))
			return false;

		Class c = abr.getTask().getTarget().getClass();

		return isSubclasses()
		  ?(getTargetClass().isAssignableFrom(c))
		  :(getTargetClass().equals(c));
	}

	protected void    invokeBuilders(ActionBuildRec abr)
	{
		for(ActionBuilder ab : getReference().dereferObjects())
		{
			//!: invoke the builder referred
			ab.buildAction(abr);

			//?: {the build is complete} exit
			if(abr.isComplete())
				return;
		}
	}

	/* private: dispatcher selector class  */

	private Class   targetClass;
	private boolean subclasses;

	/* private: action builders reference */

	private ActionBuilderReference reference;
}