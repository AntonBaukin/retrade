package com.tverts.retrade.domain.selset;

/* Java */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionsCollection.DeleteEntity;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: retrade domain (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.retrade.domain.ActionBuilderReTrade;

/* com.tverts: support */

import com.tverts.support.logic.Predicate;


/**
 * Actions builder for Selection Sets.
 *
 * @author anton.baukin@gmail.com
 */
public class ActSelSet extends ActionBuilderReTrade
{
	/* action types */

	/**
	 * Saves the selection set if it doesn't exist.
	 * If {@link #OBJECTS}, adds them.
	 */
	public static final ActionType ENSURE =
	  ActionType.ENSURE;

	/**
	 * Deletes this selection set. Note that
	 * the default set (with empty name) may
	 * not be deleted, it would be cleared.
	 */
	public static final ActionType DELETE =
	  ActionType.DELETE;

	/**
	 * Adds {@link #OBJECTS} to the selection.
	 */
	public static final ActionType ADD    =
	  new ActionType(SelSet.class, "add");

	/**
	 * Clears the Selection Set. If {@link #OBJECTS}
	 * are set removes only them.
	 */
	public static final ActionType CLEAR  =
	  new ActionType(SelSet.class, "clear");


	/* action builder parameters */

	/**
	 * Parameter with collection, or array, or single
	 * Number (or long) primary keys of Primary
	 * Identity entities to process. Entities and nested
	 * collections may be placed also.
	 */
	public static final String OBJECTS    =
	  ActSelSet.class.getName() + ": selected objects";

	/**
	 * The same as {@link #OBJECTS}, but contains the
	 * keys of Selection Set Items.
	 */
	public static final String ITEMS      =
	  ActSelSet.class.getName() + ": selected items";


	/* public: ActionBuilder interface */

	public void    buildAction(ActionBuildRec abr)
	{
		if(ENSURE.equals(actionType(abr)))
			ensureSelSet(abr);

		if(ADD.equals(actionType(abr)))
			addToSelSet(abr);

		if(CLEAR.equals(actionType(abr)))
			clearSelSet(abr);

		if(DELETE.equals(actionType(abr)))
			deleteSelSet(abr);
	}


	/* protected: action methods */

	protected void ensureSelSet(ActionBuildRec abr)
	{
		//?: {target is not an Selection Set}
		checkTargetClass(abr, SelSet.class);

		//?: {has objects keys} add them
		Set<Long> keys = this.objects(abr);
		if(keys != null)
			chain(abr).first(new AddSelSetKeys(task(abr), keys));

		//~: ensure the selection set
		chain(abr).first(new SaveNumericIdentified(task(abr)).
		  setForceTest(isTestInstance(target(abr, SelSet.class).getLogin())).
		  setPredicate(new SelSetNotFound())
		);

		complete(abr);
	}

	protected void addToSelSet(ActionBuildRec abr)
	{
		//?: {target is not an Selection Set}
		checkTargetClass(abr, SelSet.class);

		//?: {has objects keys} add them
		Set<Long> keys = this.objects(abr);
		if(keys != null)
			chain(abr).first(new AddSelSetKeys(task(abr), keys));

		complete(abr);
	}

	protected void clearSelSet(ActionBuildRec abr)
	{
		//?: {target is not an Selection Set}
		checkTargetClass(abr, SelSet.class);

		//~: add remove (clear) action
		chain(abr).first(new RemoveSelSetKeys(
		  task(abr), objects(abr), items(abr)));

		complete(abr);
	}

	protected void deleteSelSet(ActionBuildRec abr)
	{
		//?: {target is not an Selection Set}
		checkTargetClass(abr, SelSet.class);

		//~: delete the selection set <-- executed the last
		chain(abr).first(new DeleteEntity(task(abr)));

		//~: add remove (clear) action
		chain(abr).first(new RemoveSelSetKeys(task(abr)));

		complete(abr);
	}


	/* protected: object keys collector */

	protected Set<Long> objects(ActionBuildRec abr)
	{
		Object objs = param(abr, OBJECTS);
		if(objs == null) return null;

		HashSet<Long> res = new HashSet<>();
		ids(objs, res);

		return res;
	}

	protected Set<Long> items(ActionBuildRec abr)
	{
		Object objs = param(abr, ITEMS);
		if(objs == null) return null;

		HashSet<Long> res = new HashSet<>();
		ids(objs, res);

		return res;
	}

	protected void      ids(Object obj, Set<Long> res)
	{
		if(obj instanceof Number)
		{
			res.add(((Number)obj).longValue());
			return;
		}

		if(obj instanceof long[])
		{
			for(long pk : (long[])obj)
				res.add(pk);
			return;
		}

		if(obj instanceof NumericIdentity)
		{
			res.add(((NumericIdentity)obj).getPrimaryKey());
			return;
		}

		if(obj instanceof Object[])
			obj = Arrays.asList((Object[]) obj);

		if(obj instanceof Collection)
			for(Object o : (Collection)obj)
				ids(o, res);
	}


	/* selection set actions */

	protected static class SelSetNotFound implements Predicate
	{
		public boolean evalPredicate(Object ctx)
		{
			ActionWithTxBase a = (ActionWithTxBase)ctx;
			SelSet           s = (SelSet) a.getTask().getTarget();

			return (bean(GetSelSet.class).
			  getSelSet(s.getLogin().getPrimaryKey(), s.getName()) == null);
		}
	}

	public static class AddSelSetKeys extends ActionWithTxBase
	{
		/* public: constructor */

		public AddSelSetKeys(ActionTask task, Set<Long> keys)
		{
			super(task);
			this.keys = keys;
		}


		/* public: Action interface */

		public Object  getResult()
		{
			return target(SelSet.class);
		}

		@SuppressWarnings("unchecked")
		protected void execute()
		{
			// select si.object from SelItem si where (si.selSet = :selSet)

			//~: select existing items
			SelSet    selset   = target(SelSet.class);
			Set<Long> existing = new HashSet<>((List<Long>) session().
			  createQuery("select si.object from SelItem si where (si.selSet = :selSet)").
			  setParameter("selSet", selset).
			  list());

			//~: remove existing keys
			keys.removeAll(existing);

			//~: add the new ones
			for(Long key : keys)
			{
				SelItem si = new SelItem();

				//~: primary key
				setPrimaryKey(session(), si, isTestInstance(selset));

				//~: selection set
				si.setSelSet(selset);

				//~: object key
				si.setObject(key);

				//!: save it
				session().save(si);
			}
		}

		/* the keys to add */

		protected final Set<Long> keys;
	}

	public static class RemoveSelSetKeys extends ActionWithTxBase
	{
		/* public: constructor */

		/**
		 * Removes all the keys.
		 */
		public RemoveSelSetKeys(ActionTask task)
		{
			super(task);
			this.keys = null;
			this.ids  = null;
		}

		/**
		 * Removes the keys selected.
		 */
		public RemoveSelSetKeys(ActionTask task, Set<Long> keys, Set<Long> ids)
		{
			super(task);
			this.keys = keys;
			this.ids  = ids;
		}


		/* public: Action interface */

		public Object  getResult()
		{
			return target(SelSet.class);
		}

		@SuppressWarnings("unchecked")
		protected void execute()
		{
			GetSelSet get = bean(GetSelSet.class);

			//?: {clear all}
			if((keys == null) && (ids == null))
				get.clearSelSet(target(SelSet.class).getPrimaryKey());

			//?: {remove by the objects}
			if(keys != null)
				get.removeSelSetItemsByObjects(
				  target(SelSet.class).getPrimaryKey(), keys);

			//?: {remove directly}
			if(ids != null)
				get.removeSelSetItemsByIds(ids);
		}

		/* the keys to delete */

		protected final Set<Long> keys;
		protected final Set<Long> ids;
	}
}