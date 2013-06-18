package com.tverts.endure.secure;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.DeleteEntity;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure core */

import com.tverts.endure.ActionBuilderXRoot;

/* com.tverts: support */

import com.tverts.support.logic.Predicate;


/**
 * Action Builder for Secure Ables.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSecAble extends ActionBuilderXRoot
{
	/**
	 * Checks whether the given Secure Able exists
	 * and saves, if not. Note that the assign set
	 * of the Able is also checked.
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;

	public static final ActionType SAVE   =
	  ActionType.SAVE;

	public static final ActionType DELETE =
	  ActionType.DELETE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecAble(abr);

		if(SAVE.equals(actionType(abr)))
			saveSecAble(abr);

		if(DELETE.equals(actionType(abr)))
			deleteSecAble(abr);
	}


	/* protected: action methods */

	protected void ensureSecAble(ActionBuildRec abr)
	{
		//?: {target is not a Secure Able}
		checkTargetClass(abr, SecAble.class);

		//?: {has no Secure Set} create the default
		SecAble able = target(abr, SecAble.class);
		boolean setx = (able.getSet() == null); if(setx)
		{
			SecSet s; able.setSet(s = new SecSet());

			//~: domain
			s.setDomain(able.getDomain());

			//~: default name
			s.setName("");
		}

		//~: save the able
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setPredicate(new SecAbleMissing()));

		//?: {the default set was created here} ensure it
		if(setx) xnest(abr, ActionType.ENSURE, able.getSet());

		complete(abr);
	}

	protected void saveSecAble(ActionBuildRec abr)
	{
		//?: {target is not a Secure Able}
		checkTargetClass(abr, SecAble.class);

		//~: save the able
		chain(abr).first(new SaveNumericIdentified(task(abr)));

		complete(abr);
	}

	protected void deleteSecAble(ActionBuildRec abr)
	{
		//?: {target is not a Secure Able}
		checkTargetClass(abr, SecAble.class);

		//~: delete the able
		chain(abr).first(new DeleteEntity(task(abr)));

		complete(abr);
	}


	/* secure able existing predicate */

	protected static class SecAbleMissing implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			SecAble a = (SecAble) ((ActionWithTxBase)ctx).
			  getTask().getTarget();

			//~: search for the existing able
			SecAble x = bean(GetSecure.class).getSecAble(
			  a.getRule().getPrimaryKey(), a.getLogin().getPrimaryKey(),
			  a.getSet().getPrimaryKey()
			);
			if(x == null) return true;

			//~: init the task able
			a.setPrimaryKey(x.getPrimaryKey());
			a.setAbleTime(x.getAbleTime());

			return false;
		}
	}
}