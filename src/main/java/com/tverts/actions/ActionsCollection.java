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

			this.target  = target;
			this.creator = null;
		}

		/**
		 * Saves the {@link NumericIdentity} created by the
		 * delayed creation strategy.
		 */
		public SaveNumericIdentified(ActionTask task, DelayedInstance creator)
		{
			super(task);

			this.target  = null;
			this.creator = creator;
		}


		/* public: SaveNumericIdentified interface */

		public NumericIdentity getSaveTarget()
		{
			//?: {the target must be created by strategy}
			if(creator != null)
				target = creator.createInstance(this);

			//?: {the target is provided}
			if(target != null)
				return target;

			Object t = targetOrNull();
			return (t instanceof NumericIdentity)?((NumericIdentity)t):(null);
		}

		public boolean         isDelayedCreation()
		{
			return (creator != null);
		}


		/* protected: ActionBase interface */

		protected void openValidate()
		{
			super.openValidate();

			//HINT:  for reasons of action chaining we can't
			//  test the instances that are created on the demand,
			//  as the creation strategies may rely on the data
			//  provided from previous trigger runs of the chain.

			//?: {the target is not a primary identity}
			if(!isDelayedCreation() && (getSaveTarget() == null))
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

		protected DelayedInstance creator;
		protected NumericIdentity target;
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
			//?: {is the instance already persisted}
			boolean reload = session().contains(instance);

			//?: {instance was persisted} reload it
			if(reload) instance = (OrderIndex)session().load(
			  instance.getClass(), instance.getPrimaryKey());

			//~: create the request
			request = new OrderRequest(instance, reference).
			  setBeforeAfter(isBeforeAfter());

			//HINT: reference instance must be already saved to the
			//      database and not need to be reloaded.

			//!: do order
			OrderPoint.order(request);
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
		protected OrderIndex   reference;
		protected boolean      beforeAfter;


		/* protected: order request parameters */

		protected OrderRequest request;
	}


	/* delete entity */

	/**
	 * General action of deleting an entity from the database.
	 */
	public static class DeleteEntity
	       extends      ActionWithTxBase
	{
		/* public: constructors */

		public DeleteEntity(ActionTask task)
		{
			super(task);
		}

		public DeleteEntity(ActionTask task, Object target)
		{
			super(task);
			this.target = target;
		}


		/* public: DeleteEntity (access the parameters) */

		public Object       getDeleteTarget()
		{
			return (this.target != null)?(this.target):
			  (targetOrNull());
		}

		public boolean      isFlushBefore()
		{
			return flushBefore;
		}

		public DeleteEntity setFlushBefore(boolean flushBefore)
		{
			this.flushBefore = flushBefore;
			return this;
		}

		public boolean      isFlushAfter()
		{
			return flushAfter;
		}

		public DeleteEntity setFlushAfter(boolean flushAfter)
		{
			this.flushAfter = flushAfter;
			return this;
		}


		/* protected: ActionBase interface */

		protected void execute()
		  throws Throwable
		{
			if(getDeleteTarget() == null) return;

			if(isFlushBefore())
				session().flush();

			doDelete();

			if(isFlushAfter())
				session().flush();
		}

		protected void doDelete()
		{
			session().delete(getDeleteTarget());
		}

		/**
		 * Returns the reference to the entity deleted.
		 */
		public Object  getResult()
		{
			return getDeleteTarget();
		}


		/* private: parameters of the action */

		private Object  target;
		private boolean flushBefore;
		private boolean flushAfter;
	}
}