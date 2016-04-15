package com.tverts.endure.secure;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure core */

import com.tverts.endure.ActionBuilderXRoot;

/* com.tverts: support */

import com.tverts.support.logic.Predicate;


/**
 * Action Builder for Secure Keys.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSecKey extends ActionBuilderXRoot
{
	/**
	 * Checks whether the key given exists
	 * (by it's unique name), and saves, if not.
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecKey(abr);
	}


	/* protected: action methods */

	protected void ensureSecKey(ActionBuildRec abr)
	{
		//?: {target is not a Secure Key}
		checkTargetClass(abr, SecKey.class);

		//~: save the key
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setPredicate(new SecKeyMissing()));

		complete(abr);
	}


	/* secure key existing predicate */

	protected static class SecKeyMissing implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			SecKey k = (SecKey) ((ActionWithTxBase)ctx).
			  getTask().getTarget();

			return (bean(GetSecure.class).getSecKey(k.getName()) == null);
		}
	}
}