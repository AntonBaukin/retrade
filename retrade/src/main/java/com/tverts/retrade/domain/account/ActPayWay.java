package com.tverts.retrade.domain.account;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.ActionBuilderReTrade;


/**
 * Action builder to save payment ways of classes
 * {@link PayBank}, and {@link PayCash}.
 *
 * The Unity Types are selected automatically
 * as {@link Accounts} constants. You may
 * overwrite the type explicitly defining
 * parameter {@link ActUnity#UNITY_TYPE}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActPayWay extends ActionBuilderReTrade
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
		if(PayBank.class.equals(targetClass(abr)))
			savePayBank(abr);

		if(PayCash.class.equals(targetClass(abr)))
			savePayCash(abr);
	}

	protected void savePayBank(ActionBuildRec abr)
	{
		//?: {target is not a bank destination}
		checkTargetClass(abr, PayBank.class);

		//~: save the payment destination
		savePayWay(abr);

		complete(abr);
	}

	protected void savePayCash(ActionBuildRec abr)
	{
		//?: {target is not a cash desk destination}
		checkTargetClass(abr, PayCash.class);

		//~: save the payment destination
		savePayWay(abr);

		complete(abr);
	}

	protected void savePayWay(ActionBuildRec abr)
	{
		//~: save the payment way
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the payment way unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, choosePayWayType(abr));
	}


	/* protected: builder support */

	protected UnityType choosePayWayType(ActionBuildRec abr)
	{
		UnityType ut = paramPayWayType(abr);
		if(ut != null) return ut;

		return UnityTypes.unityType(targetClass(abr));
	}

	protected UnityType paramPayWayType(ActionBuildRec abr)
	{
		Object ut = param(abr, ActUnity.UNITY_TYPE);

		if(ut instanceof UnityType)
			return (UnityType)ut;

		if(ut instanceof String)
			return UnityTypes.unityType(targetClass(abr), ut.toString());

		return UnityTypes.unityType(targetClass(abr));
	}
}