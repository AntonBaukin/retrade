package com.tverts.endure.msg;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Actions for the Message Boxes.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActMsgBox extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;


	/* Action Builder */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveMsgBox(abr);
	}


	/* protected: action methods */

	protected void saveMsgBox(ActionBuildRec abr)
	{
		//?: {target is not a Message Box}
		checkTargetClass(abr, MsgBoxObj.class);

		//?: {box has the login assigned}
		MsgBoxObj mb = target(abr, MsgBoxObj.class);
		EX.assertn(mb.getLogin());

		//~: ensure ox
		mb.getOx(); //<-- side-effect
		mb.updateOx();

		//~: save the box
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setOwner(mb.getLogin()));

		complete(abr);
	}
}