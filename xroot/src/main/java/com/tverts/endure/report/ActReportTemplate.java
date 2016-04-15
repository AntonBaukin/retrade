package com.tverts.endure.report;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.DeleteEntity;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.core.ActUnity;


/**
 * Actions to save and update Report Templates.
 *
 * @author anton.baukin@gmail.com.
 */
public class ActReportTemplate extends ActionBuilderXRoot
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;

	public static final ActionType DELETE =
	  ActionType.DELETE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveReportTemplate(abr);

		if(UPDATE.equals(actionType(abr)))
			updateReportTemplate(abr);

		if(DELETE.equals(actionType(abr)))
			deleteReportTemplate(abr);
	}


	/* protected: action methods */

	protected void saveReportTemplate(ActionBuildRec abr)
	{
		//?: {target is not a Report Template}
		checkTargetClass(abr, ReportTemplate.class);

		//~: save the good unit
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set report template' unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void updateReportTemplate(ActionBuildRec abr)
	{
		//?: {target is not a Report Template}
		checkTargetClass(abr, ReportTemplate.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}

	protected void deleteReportTemplate(ActionBuildRec abr)
	{
		//?: {target is not a Report Template}
		checkTargetClass(abr, ReportTemplate.class);

		//~: delete action
		chain(abr).first(new DeleteEntity(task(abr)));

		complete(abr);
	}
}