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
 * Action Builder for Secure Links.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSecLink extends ActionBuilderXRoot
{
	/**
	 * Checks whether the given Secure Link exists
	 * and saves, if not. The triple of (key,
	 * rule, and target), but not deny-allow flag
	 * is compared.
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecLink(abr);
	}


	/* protected: action methods */

	protected void ensureSecLink(ActionBuildRec abr)
	{
		//?: {target is not a Secure Link}
		checkTargetClass(abr, SecLink.class);

		//~: save the link
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setPredicate(new SecLinkMissing()));

		complete(abr);
	}


	/* secure able existing predicate */

	protected static class SecLinkMissing implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			SecLink l = (SecLink) ((ActionWithTxBase)ctx).
			  getTask().getTarget();

			return (bean(GetSecure.class).getSecLink(
			  l.getKey(), l.getRule(), l.getTarget().getPrimaryKey()) == null);
		}
	}
}