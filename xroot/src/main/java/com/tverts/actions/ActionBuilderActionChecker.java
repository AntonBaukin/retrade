package com.tverts.actions;

/* standard Java classes */

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/* com.tverts: actions */

import static com.tverts.actions.ActionBuildersRoot.STEP_TASK;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * This action builder checker does check not the
 * build target instance, but only the action type.
 *
 * The type is defined by two parameters: goal class,
 * and the type name. The target class must equal to
 * the action goal class.
 *
 * Note that the target class is not checked, but the
 * action goal class is checked always.
 *
 * (Technical: works on task phase only.)
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   ActionBuilderActionChecker
       extends ActionBuilderCheckerBase
{
	/* public: ActionBuilderActionChecker (bean) interface */

	public Class  getGoalClass()
	{
		return goalClass;
	}

	public void   setGoalClass(Class goalClass)
	{
		this.goalClass = goalClass;
	}

	/**
	 * Defines the action names to check separated by ','.
	 * When not defined the name is not checked, only the
	 * goal class.
	 */
	public String getActionNames()
	{
		return (actionNames == null)?(null):SU.a2s(actionNames);
	}

	public void   setActionNames(String actionNames)
	{
		this.actionNames = (actionNames == null)?(null):
		  (new LinkedHashSet<String>(Arrays.asList(SU.s2a(actionNames))));

		if((this.actionNames != null) && this.actionNames.isEmpty())
			this.actionNames = null;
	}


	/* protected: action build dispatching */

	protected boolean   isActionBuildPossible(ActionBuildRec abr)
	{
		//?: {target step + the type needed}
		return isBuildStep(abr, STEP_TASK) &&
		  isActionTypeMatch(abr);
	}

	protected boolean   isActionTypeMatch(ActionBuildRec abr)
	{
		//?: {the goal class is not defined} illegal config
		if(getGoalClass() == null)
			return false;

		//?: {the action goal class differs}
		if(!getGoalClass().equals(actionType(abr).getGoalClass()))
			return false;

		//?: {the action names are note specified | that name} matched
		return (actionNames() == null) ||
		  actionNames().contains(actionType(abr).getActionName());
	}

	protected Set<String> actionNames()
	{
		return actionNames;
	}


	/* private: dispatcher selector class  */

	private Class       goalClass;
	private Set<String> actionNames;
}