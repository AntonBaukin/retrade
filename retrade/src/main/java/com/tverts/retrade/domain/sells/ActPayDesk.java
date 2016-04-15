package com.tverts.retrade.domain.sells;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * {@link PayDesk} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActPayDesk extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			savePayDesk(abr);
	}


	/* protected: action methods */

	protected void savePayDesk(ActionBuildRec abr)
	{
		//?: {target is not an Pay Desk}
		checkTargetClass(abr, PayDesk.class);

		//~: save the payments desk
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the payments desk (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}
}