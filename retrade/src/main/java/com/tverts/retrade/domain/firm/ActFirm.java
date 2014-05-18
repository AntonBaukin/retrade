package com.tverts.retrade.domain.firm;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: endure (core) */

import com.tverts.endure.core.ActUnity;

/* com.tverts: system (tx) */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.system.tx.SetTxAction;

/* com.tverts: retrade domain (core) */


/**
 * {@link Firm} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActFirm extends ActionBuilderReTrade
{
	/* action types */

	/**
	 * Send task with this type to save the given firm
	 * instance (the target) and to create+save all the
	 * related instances.
	 */
	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveFirm(abr);

		if(UPDATE.equals(actionType(abr)))
			updateFirm(abr);
	}


	/* protected: action methods */

	protected void saveFirm(ActionBuildRec abr)
	{
		//?: {target is not a Firm}
		checkTargetClass(abr, Firm.class);

		//~: save the firm
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: set firm' unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr));

		complete(abr);
	}

	protected void updateFirm(ActionBuildRec abr)
	{
		//?: {target is not a Firm}
		checkTargetClass(abr, Firm.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}
}