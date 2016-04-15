package com.tverts.endure.secure;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure core */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.core.ActUnity;


/**
 * Action Builder for Secure Rules.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSecRule extends ActionBuilderXRoot
{
	/**
	 * Checks whether the rule given exists
	 * (by it's unique name), and saves, if not.
	 */
	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveSecRule(abr);
	}


	/* protected: action methods */

	protected void saveSecRule(ActionBuildRec abr)
	{
		//?: {target is not a Secure Rule}
		checkTargetClass(abr, SecRule.class);

		//~: save the rule
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set rule unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}
}