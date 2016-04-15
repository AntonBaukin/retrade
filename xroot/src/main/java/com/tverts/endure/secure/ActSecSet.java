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

/* com.tverts: endure (core + auth) */

import com.tverts.endure.ActionBuilderXRoot;
import com.tverts.endure.core.ActUnity;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;

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
	 * Ables referring to it the default Set.
	 */
	public static final ActionType DELETE =
	  ActionType.DELETE;

	/**
	 * Assigns the rules of this Secure Set to the
	 * login defined by {@link #LOGIN} parameter.
	 */
	public static final ActionType GRANT  =
	  new ActionType(SecSet.class, "grant");

	/**
	 * Revokes the rule ables of this Secure Set
	 * and the login defined by {@link #LOGIN} parameter.
	 */
	public static final ActionType REVOKE =
	  new ActionType(SecSet.class, "revoke");

	/**
	 * Synchronizes the rule ables of this Secure Set
	 * and the login defined by {@link #LOGIN} parameter.
	 */
	public static final ActionType SYNCH  =
	  new ActionType(SecSet.class, "synch");


	/* parameters */

	/**
	 * Defines {@link AuthLogin} instance to
	 * grant (able) the rules of this set.
	 */
	public static final String  LOGIN =
	  ActSecSet.class.getName() + ": login";



	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSecSet(abr);

		if(DELETE.equals(actionType(abr)))
			deleteSecSet(abr);

		if(GRANT.equals(actionType(abr)))
			grantSecSet(abr);

		if(REVOKE.equals(actionType(abr)))
			revokeSecSet(abr);

		if(SYNCH.equals(actionType(abr)))
			synchSecSet(abr);
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

	protected void grantSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		GetSecure get = bean(GetSecure.class);
		SecSet    set = target(abr, SecSet.class);
		AuthLogin lgn = param(abr, LOGIN, AuthLogin.class);
		if(lgn == null) throw EX.state("AuthLogin (LOGIN) parameter is missing!");

		//~: system login (of the set domain)
		Long      sys = bean(GetAuthLogin.class).
		  getSystemLogin(set.getDomain().getPrimaryKey()).getPrimaryKey();

		//~: load the rules of this set
		Set<Long>  all = new HashSet<Long>(
		  get.findSetAbleRules(sys, set.getPrimaryKey()));

		//~: load the rules able for the user in this set
		List<Long> got = get.findSetAbleRules(
		  lgn.getPrimaryKey(), set.getPrimaryKey());

		//~: assign the missing rules
		all.removeAll(got);
		for(Long rule : all)
		{
			SecAble able = new SecAble();

			//~: login
			able.setLogin(lgn);

			//~: rule
			able.setRule(get.getSecRule(rule));

			//~: set
			able.setSet(set);

			//!: save it
			xnest(abr, ActionType.SAVE, able);
		}

		complete(abr);
	}

	protected void revokeSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		GetSecure get = bean(GetSecure.class);
		SecSet    set = target(abr, SecSet.class);
		AuthLogin lgn = param(abr, LOGIN, AuthLogin.class);
		if(lgn == null) throw EX.state("AuthLogin (LOGIN) parameter is missing!");

		//~: load the ables for the user in this set
		List<SecAble> ables = get.findSetAbles(
		  lgn.getPrimaryKey(), set.getPrimaryKey());

		//~: assign the missing rules
		for(SecAble able : ables)
			xnest(abr, ActionType.DELETE, able);

		complete(abr);
	}

	protected void synchSecSet(ActionBuildRec abr)
	{
		//?: {target is not a Secure Set}
		checkTargetClass(abr, SecSet.class);

		GetSecure get = bean(GetSecure.class);
		SecSet    set = target(abr, SecSet.class);
		AuthLogin lgn = param(abr, LOGIN, AuthLogin.class);
		if(lgn == null) throw EX.state("AuthLogin (LOGIN) parameter is missing!");

		//~: system login (of the set domain)
		Long      sys = bean(GetAuthLogin.class).
		  getSystemLogin(set.getDomain().getPrimaryKey()).getPrimaryKey();

		//~: load the ables for the user in this set
		List<SecAble> got = get.findSetAbles(
		  lgn.getPrimaryKey(), set.getPrimaryKey());

		//~: get rules of current ables of the set
		Set<Long> curules = new HashSet<Long>(
		  get.findSetAbleRules(sys, set.getPrimaryKey()));

		//~: remove the ables with rules missing in the current
		for(SecAble a : got)
			if(!curules.contains(a.getRule().getPrimaryKey()))
				xnest(abr, ActionType.DELETE, a);

		//~: collect the rules of able sets
		Set<Long> gotrules = new HashSet<Long>(got.size());
		for(SecAble a : got) gotrules.add(a.getRule().getPrimaryKey());

		//~: add the rules not able to the login
		for(Long rule : curules)
			if(!gotrules.contains(rule))
			{
				SecAble able = new SecAble();

				//~: login
				able.setLogin(lgn);

				//~: able rule
				able.setRule(get.getSecRule(rule));

				//~: set
				able.setSet(set);

				//!: ensure it
				xnest(abr, ActionType.ENSURE, able);
			}

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

			//?: {has this in the context}
			SecSet x = (SecSet)((ActionWithTxBase)ctx).getActionTx().val(s.altKey());

			//~: search in the database
			if(x == null) x = bean(GetSecure.class).getSecSet(
			  s.getDomain().getPrimaryKey(), s.getName());

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
			//flush(session());
			session().delete(set);
		}
	}
}