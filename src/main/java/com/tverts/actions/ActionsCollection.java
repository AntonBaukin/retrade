package com.tverts.actions;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;

/* com.tverts: endure (ordering) */

import com.tverts.endure.order.OrderIndex;
import com.tverts.endure.order.OrderPoint;
import com.tverts.endure.order.OrderRequest;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * A collection of actions shared between
 * various action builders.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionsCollection
{
	/* save primary instance */

	/**
	 * Saves given {@link NumericIdentity} instance.
	 * If it has no primary key, the key would be generated.
	 */
	public static class SaveNumericIdentified
	       extends      ActionWithTxBase
	{
		/* public: constructors */

		/**
		 * Saves the {@link NumericIdentity} taken
		 * as the target of the task.
		 */
		public SaveNumericIdentified(ActionTask task)
		{
			super(task);
			this.target = null;
		}

		/**
		 * Saves the {@link NumericIdentity} provided explicitly.
		 */
		public SaveNumericIdentified(ActionTask task, NumericIdentity target)
		{
			super(task);
			this.target = target;
		}


		/* public: SaveNumericIdentified interface */

		public NumericIdentity getSaveTarget()
		{
			if(target != null) return target;

			Object t = targetOrNull();
			return (t instanceof NumericIdentity)?((NumericIdentity)t):(null);
		}


		/* protected: ActionBase interface */

		protected void openValidate()
		{
			super.openValidate();

			//?: {the target is not a primary identity}
			if(getSaveTarget() == null)
				throw new IllegalStateException(String.format(
				  "Can't save undefined entity, or of the class '%s' " +
				  "not a NumericIdentity!", OU.cls(targetOrNull())
				));
		}

		protected void execute()
		  throws Throwable
		{
			//~: set the primary key
			setPrimaryKey();

			//!: save
			doSave();
		}

		public Object  getResult()
		{
			return getSaveTarget();
		}


		/* protected: execution */

		protected void setPrimaryKey()
		{
			NumericIdentity e = getSaveTarget();

			//?: {entity still has no primary key} generate it
			if(e.getPrimaryKey() == null)
				HiberPoint.setPrimaryKey(session(), e);
		}

		protected void doSave()
		{
			//!: invoke save
			session().save(getSaveTarget());
		}


		/* protected: the alternative target */

		protected final NumericIdentity target;
	}


	/* set order index */

	public static class SetOrderIndex
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public SetOrderIndex(ActionTask task, OrderIndex instance, OrderIndex reference)
		{
			super(task);

			this.instance = instance;
			this.reference = reference;
		}


		/* public: SetOrderIndex interface */

		public OrderIndex    getInstance()
		{
			return instance;
		}

		public OrderIndex    getReference()
		{
			return reference;
		}

		/**
		 * Has the same meaning as {@link OrderRequest#isBeforeAfter()}.
		 */
		public boolean       isBeforeAfter()
		{
			return beforeAfter;
		}

		public SetOrderIndex setBeforeAfter(boolean beforeAfter)
		{
			this.beforeAfter = beforeAfter;
			return this;
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			//HINT: before and after the ordering we must
			//  flush and clear Hibernate session, as ordering
			//  is made via HQL update queries.

			//?: {is the instance already persisted}
			boolean reload = session().contains(instance);

			//!: save all to the database
			session().flush();
			session().clear();

			//?: {instance was persisted} reload it
			if(reload) instance = (OrderIndex)session().load(
			  instance.getClass(), instance.getPrimaryKey());

			//~: create the request
			request = new OrderRequest(instance, reference).
			  setBeforeAfter(isBeforeAfter());

			//HINT: reference instance must be already saved to the
			//      database and not needed to be reloaded.

			//!: do order
			OrderPoint.order(request);

			//!: save all to the database
			session().flush();
			session().clear();
		}

		/**
		 * @return  {@link OrderRequest} instance created.
		 */
		public Object  getResult()
		{
			return request;
		}


		/* protected: order request parameters */

		protected OrderIndex   instance;
		protected  OrderIndex  reference;
		protected boolean      beforeAfter;


		/* protected: order request parameters */

		protected OrderRequest request;
	}


}