package com.tverts.endure.secure;

/* standard Java classes */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;

/* com.tverts: endure (core) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.core.ActUnity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
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

	/**
	 * Removes the Secure Set moving all the
	 * Ables referring to to the default Set.
	 */
	public static final ActionType DELETE =
	  ActionType.DELETE;


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecSet(abr);

		if(DELETE.equals(actionType(abr)))
			deleteSecSet(abr);
	}


	/* protected: action methods */

	protected void ensureSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		//?: {has no create time} assign now
		if(target(abr, SecSet.class).getCreateTime() == null)
			target(abr, SecSet.class).setCreateTime(new java.util.Date());

		//~: set exists predicate
		Predicate p = new SecSetMissing();

		//~: save the set
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setPredicate(p));

		//~: set set unity (is executed first!)
		xnest(abr, ActUnity.CREATE, target(abr), PREDICATE, p);

		complete(abr);
	}

	protected void deleteSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		//?: {can't remove the default set}
		if(SU.sXe(target(abr, SecSet.class).getName()))
			throw EX.state("Can't delete the default Secure Set!");

		//~: delete the set
		chain(abr).first(new DeleteSelSetAction(task(abr)));

		complete(abr);
	}


	/* secure set existing predicate */

	protected static class SecSetMissing implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			if(result != null) return result;

			SecSet s = (SecSet) ((ActionWithTxBase)ctx).
			  getTask().getTarget();

			//~: search for the existing set
			SecSet x = bean(GetSecure.class).
			  getSecSet(s.getDomain().getPrimaryKey(), s.getName());
			if(x == null) return result = true;

			//~: init the task set
			s.setPrimaryKey(x.getPrimaryKey());
			s.setUnity(x.getUnity());
			s.setCreateTime(x.getCreateTime());
			s.setComment(x.getComment());
			s.setTxn(x.getTxn());

			return result = false;
		}

		protected Boolean result;
	}


	/* delete action */

	protected static class DeleteSelSetAction
	          extends      ActionWithTxBase
	{
		/* public: constructor */

		public DeleteSelSetAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public Object  getResult()
		{
			return target();
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			SecSet    set  = target(SecSet.class);
			GetSecure get  = bean(GetSecure.class);

			//~: get the default set
			SecSet    def  = get.getDefaultSecSet(
			  set.getDomain().getPrimaryKey());

			//~: find ables that refer the same rules as the default set
			Set<Long> dups = new HashSet<Long>(
			  get.findSecAblesWithDuplicates(set, def));

			//!: do remove that ables (as duplicates)
			for(Long id : dups)
				session().delete(session().load(SecAble.class, id));


			//~: get all the ables referring
			List<Long> all = get.findSecAbles(set);

			//~: update ables that do not have duplicate
			for(Long id : all) if(!dups.contains(id))
				((SecAble) session().load(SecAble.class, id)).setSet(def);


			//!: do remove the set
			session().flush();
			session().delete(set);
		}
	}
}