package com.tverts.retrade.domain.goods;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;

/* com.tverts: transactions */

import com.tverts.retrade.domain.ActionBuilderReTrade;
import com.tverts.system.tx.SetTxAction;

/* com.tverts: retrade domain core */


/**
 * {@link MeasureUnit} processing actions builder.
 *
 * @author anton.baukin@gmail.com
 */
public class ActMeasureUnit extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveMeasureUnit(abr);

		if(UPDATE.equals(actionType(abr)))
			updateMeasureUnit(abr);
	}


	/* protected: action methods */

	protected void saveMeasureUnit(ActionBuildRec abr)
	{
		//?: {target is not a MeasureUnit}
		checkTargetClass(abr, MeasureUnit.class);

		//~: save the firm
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		complete(abr);
	}

	protected void updateMeasureUnit(ActionBuildRec abr)
	{
		//?: {target is not a MeasureUnit}
		checkTargetClass(abr, MeasureUnit.class);

		//~: update the Txn
		chain(abr).first(new SetTxAction(task(abr)));

		complete(abr);
	}
}