package com.tverts.endure.core;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderWithTxBase;
import com.tverts.actions.ActionsCollection.SavePrimaryIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure */


import com.tverts.endure.UnityType;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Action build strategy to save given
 * {@link UnityType} instances.
 *
 * @author anton.baukin@gmail.com
 */
public class ActUnityType extends ActionBuilderWithTxBase
{
	/* action types */

	/**
	 * Action type of the task to save {@link UnityType}
	 * instance given as the target.
	 */
	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveUnityType(abr);
	}


	/* protected: action methods */

	protected void saveUnityType(ActionBuildRec abr)
	{
		//?: {the target is not a united}
		checkTargetClass(abr, UnityType.class);

		//~: add action to the chain
		chain(abr).first(new SavePrimaryIdentified(task(abr)));

		//!: complete the build
		complete(abr);
	}
}