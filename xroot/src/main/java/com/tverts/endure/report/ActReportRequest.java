package com.tverts.endure.report;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;


/**
 * Action to save Report Requests.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActReportRequest extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveReportTemplate(abr);
	}


	protected void saveReportTemplate(ActionBuildRec abr)
	{
		//?: {target is not a Report Request}
		checkTargetClass(abr, ReportRequest.class);

		//~: save the good unit
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		complete(abr);
	}
}