package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.util.Arrays;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.aggr.ActAggrValue;
import com.tverts.endure.aggr.calc.AggrCalcs;
import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * Action Builders to save {@link PayIt} links.
 *
 * The Unity Type of the Payment Link is selected
 * based on the actual class of the entity.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActPayIt extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE =
	  ActionType.SAVE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveAction(abr);
	}


	/* protected: action methods */

	protected void saveAction(ActionBuildRec abr)
	{
		if(PaySelf.class.equals(targetClass(abr)))
			savePaySelf(abr);

		if(PayFirm.class.equals(targetClass(abr)))
			savePayFirm(abr);
	}

	protected void savePaySelf(ActionBuildRec abr)
	{
		//?: {target is not a self payment link}
		checkTargetClass(abr, PaySelf.class);

		//~: create payment link balance aggregated value
		ensurePayItAggrValues(abr);

		//~: save the payment link
		savePayIt(abr);

		complete(abr);
	}

	protected void savePayFirm(ActionBuildRec abr)
	{
		//?: {target is not a contractor payment link}
		checkTargetClass(abr, PayFirm.class);

		//~: create payment link balance aggregated value
		ensurePayItAggrValues(abr);

		//~: save the contractor payment link
		savePayIt(abr);

		complete(abr);
	}

	protected void savePayIt(ActionBuildRec abr)
	{
		//~: save the payment way
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the payment way unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, choosePayItType(abr));
	}

	protected void ensurePayItAggrValues(ActionBuildRec abr)
	{
		//~: ensure debt aggregated value
		buildAggrValue(abr, Accounts.AGGRVAL_PAYIT_BALANCE, null,
		  ActAggrValue.CALCS, Arrays.asList(
		    AggrCalcs.AGGR_CALC_VOL_MONTH,
		    AggrCalcs.AGGR_CALC_VOL_WEEK
		));
	}


	/* protected: builder support */

	protected UnityType choosePayItType(ActionBuildRec abr)
	{
		UnityType ut = paramPayItType(abr);
		if(ut != null) return ut;

		return UnityTypes.unityType(targetClass(abr));
	}

	protected UnityType paramPayItType(ActionBuildRec abr)
	{
		Object ut = param(abr, ActUnity.UNITY_TYPE);

		if(ut instanceof UnityType)
			return (UnityType)ut;

		if(ut instanceof String)
			return UnityTypes.unityType(targetClass(abr), ut.toString());

		return UnityTypes.unityType(targetClass(abr));
	}
}