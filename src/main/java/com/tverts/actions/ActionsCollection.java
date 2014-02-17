package com.tverts.actions;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.United;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.UnityView;

/* com.tverts: endure (ordering) */

import com.tverts.endure.order.OrderIndex;
import com.tverts.endure.order.OrderPoint;
import com.tverts.endure.order.OrderRequest;

/* com.tverts: events */

import com.tverts.event.ActiveEvent;
import com.tverts.event.Event;
import com.tverts.event.EventPoint;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.logic.Predicate;


/**
 * A collection of actions shared between
 * various action builders.
 *
 * @author anton.baukin@gmail.com
 */
public class ActionsCollection
{
	/* ensure predicate */

	public static interface EnsurePredicate extends Predicate
	{
		/* public: EnsurePredicate interface */

		/**
		 * Invoked only after predicate evaluation.
		 * If instance already exists (and predicate had
		 * returned false), must return that instance
		 * to be the result of action execution.
		 */
		public NumericIdentity getExistingInstance();
	}


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

		public NumericIdentity       getSaveTarget()
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

		public boolean               isDelayedCreation()
		{
			return (creator != null);
		}

		public boolean               isForceTest()
		{
			return forceTest;
		}

		public SaveNumericIdentified setForceTest(boolean forceTest)
		{
			this.forceTest = forceTest;
			return this;
		}

		public SaveNumericIdentified setOwner(NumericIdentity owner)
		{
			this.owner = owner;
			return this;
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
				  "not a NumericIdentity!", LU.cls(targetOrNull())
				));
		}

		protected void execute()
		  throws Throwable
		{
			//~: set the primary key
			setPrimaryKey();

			//~: assign the fields
			assignSaveTarget();

			//~: set the transaction number
			TxPoint.txn(tx(), getSaveTarget());

			//!: save
			doSave();
		}

		public Object  getResult()
		{
			NumericIdentity e = null;

			if(getPredicate() instanceof EnsurePredicate)
				e = ((EnsurePredicate)getPredicate()).getExistingInstance();

			if(e == null)
				e =  getSaveTarget();

			return e;
		}


		/* protected: save execution */

		protected void    setPrimaryKey()
		{
			NumericIdentity e = getSaveTarget();

			//?: {entity still has no primary key} generate it
			if(e.getPrimaryKey() == null)
				HiberPoint.setPrimaryKey(session(), e, isTestSaveTarget());
		}

		protected boolean isTestSaveTarget()
		{
			if(isForceTest()) return true;

			Object  target  = getSaveTarget();
			Object  context = targetOrNull();
			boolean result  = false;

			//?: {there is an owner}
			if(this.owner != null)
				result = checkTestInstance(this.owner);
			if(result) return true;

			//?: {the target differs from it's context} see the context
			if((target != context) && (context instanceof NumericIdentity))
				result = checkTestInstance(context);

			return result || checkTestInstance(target);
		}

		protected boolean checkTestInstance(Object obj)
		{
			//?: {the domain is for tests}
			return (obj instanceof NumericIdentity) &&
			  HiberPoint.isTestInstance((NumericIdentity)obj);
		}

		protected void    assignSaveTarget()
		{}

		protected void    doSave()
		{
			//!: invoke save
			session().save(getSaveTarget());
		}


		/* protected: the alternative target */

		protected DelayedInstance creator;
		protected NumericIdentity target;
		protected NumericIdentity owner;


		/* private: save parameters */

		private boolean forceTest;
	}


	/* save view base */

	/**
	 * Assigns properties of the target {@link UnityView}
	 * from the given owning entity.
	 */
	public static class SaveViewBase
	       extends      SaveNumericIdentified
	{
		/* public: constructors */

		public SaveViewBase(ActionTask task, UnityView view, United viewOwner)
		{
			super(task, view);
			this.viewOwner = viewOwner;
		}

		public SaveViewBase(ActionTask task, DelayedInstance creator, United viewOwner)
		{
			super(task, creator);
			this.viewOwner = viewOwner;
		}


		/* public: SaveViewBase interface */

		public United     getViewOwner()
		{
			return viewOwner;
		}


		/* public: SaveNumericIdentified interface */

		public UnityView  getSaveTarget()
		{
			return (UnityView)super.getSaveTarget();
		}


		/* protected: save execution */

		protected boolean isTestSaveTarget()
		{
			return HiberPoint.isTestInstance(getViewOwner());
		}

		protected void    assignSaveTarget()
		{
			UnityView v = getSaveTarget();
			United    o = getViewOwner();

			//~: view domain
			if(v.getDomain() == null)
				if(o instanceof DomainEntity)
					v.setDomain(((DomainEntity)o).getDomain());

			//~: view owner unity
			v.setViewOwner(o.getUnity());
		}


		/* protected: the view's owner  */

		protected United  viewOwner;
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

		public Class         getIndexClass()
		{
			return indexClass;
		}

		public SetOrderIndex setIndexClass(Class indexClass)
		{
			this.indexClass = indexClass;
			return this;
		}

		public SetOrderIndex setTester(Runnable tester)
		{
			this.tester = tester;
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
			  setIndexClass(getIndexClass()).
			  setBeforeAfter(isBeforeAfter());

			//HINT: reference instance must be already saved to the
			//      database and not need to be reloaded.

			//debug: do test order before
			if(tester != null)
				tester.run();

			//!: do order
			OrderPoint.order(request);

			//debug: do test order after
			if(tester != null)
				tester.run();
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
		protected Class        indexClass;
		protected boolean      beforeAfter;
		protected Runnable     tester;


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
				HiberPoint.flush(session());

			doDelete();

			if(isFlushAfter())
				HiberPoint.flush(session());
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


	/* event action */

	public static class EventAction extends ActionWithTxBase
	{
		/* constructor */

		public EventAction(ActionTask task, Event event)
		{
			super(task);
			if(event == null) throw new IllegalArgumentException();
			this.event = event;
		}


		/* public: EventAction interface */

		public Event   getEvent()
		{
			return event;
		}


		/* public: Object interface */

		public Object  getResult()
		{
			return getEvent();
		}


		/* protected: ActionBase interface */

		protected void execute()
		{
			Throwable error = null;

			//?: {active event} do action before
			if(getEvent() instanceof ActiveEvent)
				((ActiveEvent)getEvent()).actBefore(this);

			//!: trigger the event
			try
			{
				EventPoint.react(getEvent());
			}
			catch(Throwable e)
			{
				error = e;
			}

			//?: {active event} do action after
			if(getEvent() instanceof ActiveEvent)
				((ActiveEvent)getEvent()).actAfter(error);
			//~: handle the error
			else if(error instanceof RuntimeException)
				throw (RuntimeException) error;
			else if(error != null)
				throw new RuntimeException(error);
		}


		/* the event */

		private Event event;
	}
}