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
 * Action Builder for Secure Sets.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSecSet extends ActionBuilderXRoot
{
	/* actions */

	/**
	 * Checks whether the set given exists
	 * (by it's unique name), and saves, if not.
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecSet(abr);
	}


	/* protected: action methods */

	protected void ensureSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		//?: {has no create time} assign now
		if(target(abr, SecSet.class).getCreateTime() == null)
			target(abr, SecSet.class).setCreateTime(new java.util.Date());

		//~: save the key
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setPredicate(new SecSetMissing()));

		complete(abr);
	}


	/* secure set existing predicate */

	protected static class SecSetMissing implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			SecSet s = (SecSet) ((ActionWithTxBase)ctx).
			  getTask().getTarget();

			//~: search for the existing set
			SecSet x = bean(GetSecure.class).
			  getSecSet(s.getDomain().getPrimaryKey(), s.getCode());
			if(x == null) return true;

			//~: init the task set
			s.setPrimaryKey(x.getPrimaryKey());
			s.setCreateTime(x.getCreateTime());
			s.setComment(x.getComment());
			s.setTxn(x.getTxn());

			return false;
		}
	}
}