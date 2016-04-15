package com.tverts.retrade.domain.account;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

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

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.logic.Predicate;


/**
 * Action builder to save Accounts.
 *
 * The Unity Types are selected as
 * {@link Accounts} constants.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActAccount extends ActionBuilderReTrade
{
	/* action types */

	public static final ActionType SAVE    =
	  ActionType.SAVE;

	/**
	 * Adds the {@link PayWay} defined by
	 * {@link #PAY_WAY} to the target Account.
	 */
	public static final ActionType ADD_WAY =
	  new ActionType(Account.class, "add-payment-way");


	/* action builder parameters */

	/**
	 * Links {@link PayWay} instance.
	 */
	public static final String PAY_WAY     =
	  ActAccount.class.getName() + ": payment way";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(SAVE.equals(actionType(abr)))
			saveAccount(abr);

		if(ADD_WAY.equals(actionType(abr)))
			addPayWay(abr);
	}


	/* protected: action methods */

	protected void saveAccount(ActionBuildRec abr)
	{
		//?: {target is not an Account}
		checkTargetClass(abr, Account.class);

		//?: {there is no contractor} means own account
		if(target(abr, Account.class).getContractor() == null)
			saveOwnAccount(abr);
		else
			saveFirmAccount(abr);
	}

	protected void saveOwnAccount(ActionBuildRec abr)
	{
		//~: save the account
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the account unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, chooseOwnAccountType(abr));

		complete(abr);
	}

	protected void saveFirmAccount(ActionBuildRec abr)
	{
		//~: save the account
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		//~: create the account unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, chooseFirmAccountType(abr));

		complete(abr);
	}

	protected void addPayWay(ActionBuildRec abr)
	{
		//?: {target is not an Account}
		checkTargetClass(abr, Account.class);

		//~: get the payment way from the parameter
		PayWay w = payWay(abr);

		//~: create pay it
		PayIt  x; if(target(abr, Account.class).getContractor() == null)
			x = new PaySelf();
		else
			x = new PayFirm();

		//~: pay it account
		x.setAccount(target(abr, Account.class));

		//~: pay it way
		x.setPayWay(w);

		//~: save it...
		savePayIt(abr, x);

		complete(abr);
	}


	/* protected: builder support */

	protected UnityType chooseOwnAccountType(ActionBuildRec abr)
	{
		return UnityTypes.unityType(Account.class, Accounts.TYPE_ACCOUNT_OWN);
	}

	protected UnityType chooseFirmAccountType(ActionBuildRec abr)
	{
		return UnityTypes.unityType(Account.class, Accounts.TYPE_ACCOUNT_FIRM);
	}

	protected void      savePayIt(ActionBuildRec abr, PayIt x)
	{
		xnest(abr, ActionType.SAVE, x);
	}

	protected PayWay    payWay(ActionBuildRec abr)
	{
		return EX.assertn(
		  param(abr, PAY_WAY, PayWay.class),
		  "Payment Way parameter is not defined!"
		);
	}


	/* payment way linked predicate */

	protected static class PayWayLinked implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			PayIt x = (PayIt) ((SaveNumericIdentified) ctx).getSaveTarget();

			return (bean(GetAccount.class).
			  getPayIt(x.getAccount(), x.getPayWay()) == null);
		}
	}
}