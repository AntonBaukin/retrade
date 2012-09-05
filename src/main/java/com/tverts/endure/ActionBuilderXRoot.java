package com.tverts.endure;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestInstance;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionBuilderWithTxBase;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (aggr) */

import com.tverts.endure.aggr.ActAggrValue;
import com.tverts.endure.aggr.AggrValue;

/* com.tverts: endure (locks) */

import com.tverts.endure.locks.GetLock;
import com.tverts.endure.locks.Lock;
import com.tverts.endure.locks.Locks;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Extended action builder that provides additional
 * support for entities defined in XRoot module.
 *
 * Each module has it's own variant of such a builder.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActionBuilderXRoot
       extends        ActionBuilderWithTxBase
{
	/* parameters of the actions */

	/**
	 * Send this parameter to do aggregation synchronous.
	 *
	 * WARNING! Be careful with this feature! Synchronous
	 *   aggregation overpasses the aggregation services,
	 *   and is not blocked / synchronized. Also, the
	 *   module issuing this action must have proper
	 *   registered.
	 */
	public static final String SYNCH_AGGR =
	  ActionBuilderXRoot.class.getName() + ": synchronous aggregation";


	/* protected: aggregated values support */

	/**
	 * Creates the {@link AggrValue} of the given type
	 * for the target {@link United} instance.
	 *
	 * Note that the action created first checks whether
	 * the aggregation value of such a type already exists,
	 * and does not create one if it is so.
	 *
	 * Selector argument is optional.
	 *
	 * The owner of the aggregated value is the
	 * target of the build record. It must be
	 * {@link United} or {@link Unity} instance.
	 */
	protected void    buildAggrValue (
	                    ActionBuildRec  abr,
	                    String          aggrTypeName,
	                    NumericIdentity selector,
	                    Object...       params
	                  )
	{
		ArrayList<Object> ps = new ArrayList<Object>(4 + params.length);

		ps.add(ActAggrValue.VALUE_TYPE); ps.add(aggrTypeName);
		ps.add(ActAggrValue.SELECTOR);   ps.add(selector);
		ps.addAll(Arrays.asList(params));

		xnest(abr, ActAggrValue.CREATE, target(abr),
		  ps.toArray(new Object[ps.size()]));
	}

	/**
	 * Tells whether the aggregation request must be issued
	 * as synchronous. See {@link #SYNCH_AGGR} parameter,
	 * by default it is false.
	 *
	 * This parameter is checked recursively for the nested
	 * tasks. You do not need to pass is into the each
	 * nested task until you want to overwrite it.
	 */
	protected boolean isAggrSynch(ActionBuildRec abr)
	{
		return flagRecursive(abr, SYNCH_AGGR);
	}


	/* protected: locks support */

	protected void ensureLock(ActionBuildRec abr, United owner, UnityType type)
	{
		//~: add ensure action
		chain(abr).first(new EnsureLockAction(task(abr), owner, type));
	}

	protected void ensureLock(ActionBuildRec abr, UnityType type)
	{
		this.ensureLock(abr, target(abr, United.class), type);
	}

	protected void ensureLock(ActionBuildRec abr, String type)
	{
		this.ensureLock(abr, UnityTypes.unityType(Lock.class, type));
	}

	protected void takeLock(ActionBuildRec abr, Long owner, String type)
	{
		//~: add take action
		chain(abr).first(new LockAction(task(abr), owner,
		  UnityTypes.unityType(Lock.class, type)));
	}

	protected void takeLock(ActionBuildRec abr, PrimaryIdentity owner, String type)
	{
		//~: add take action
		chain(abr).first(new LockAction(task(abr),
		  owner.getPrimaryKey(), UnityTypes.unityType(Lock.class, type)));
	}


	/* lock action */

	public static class LockAction extends ActionWithTxBase
	{
		/* public: constructors */

		public LockAction(ActionTask task, Lock lock)
		{
			super(task);

			if(lock == null) throw new IllegalArgumentException();

			this.lock  = lock;
			this.owner = (lock.getOwner() == null)?(null):
			  (lock.getOwner().getPrimaryKey());
			this.type  = lock.getLockType();
		}

		public LockAction(ActionTask task, Long owner, UnityType type)
		{
			super(task);

			if(owner == null) throw new IllegalArgumentException();
			if(type  == null) throw new IllegalArgumentException();

			this.lock  = null;
			this.owner = owner;
			this.type  = type;
		}


		/* public: Action interface */

		public Lock      getResult()
		{
			return result;
		}

		/* protected: ActionBase interface */

		protected void   execute()
		  throws Throwable
		{
			if(this.lock == null)
				result = Locks.lock(this.owner, this.type, session());
			else
				Locks.lock(result = this.lock, session());
		}


		/* protected: action parameters */

		protected final Lock      lock;
		protected final Long      owner;
		protected final UnityType type;

		/* protected: the lock used */

		protected Lock            result;
	}


	/* ensure lock action */

	/**
	 * Checks whether the lock defined by the owner
	 * primary key and the lock type exists. If doesn't,
	 * creates a new instance.
	 */
	public static class EnsureLockAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public EnsureLockAction(ActionTask task, United owner, UnityType type)
		{
			super(task);

			if(owner == null) throw new IllegalArgumentException();
			if(type  == null) throw new IllegalArgumentException();

			this.owner = owner;
			this.type  = type;
		}


		/* public: Action interface */

		public Lock      getResult()
		{
			return lock;
		}


		/* protected: ActionBase interface */

		protected void   execute()
		  throws Throwable
		{
			//~: search for the lock
			findLock();

			//?: {exists}
			if(this.lock != null)
			{
				logLockFound();
				return;
			}

			//?: {has no Unity}
			if(owner.getUnity() == null)
				throw new IllegalStateException(String.format(
				  "Can't ensure lock of type %s owned by the United " +
				  "instace [%s] having no Unity mirror!",
				  type.toString(), owner.getPrimaryKey()
				));

			//!: create new lock
			createLock();
		}


		/* protected: execution stages */
		
		protected void   findLock()
		{
			//~: get the lock
			GetLock get = bean(GetLock.class);
			get.setSession(session());

			this.lock = get.getLock(owner.getPrimaryKey(), type);
		}

		protected void   createLock()
		{
			//~: create the new one
			this.lock = new Lock();

			//~: generate the new key (other sequence)
			setPrimaryKey(session(), this.lock,
			  isTestInstance(owner));

			//~: assign the owner
			this.lock.setOwner(owner.getUnity());

			//~: assign lock type
			this.lock.setLockType(type);

			//!: do save lock
			session().save(lock);

			logLockCreated();
		}


		/* protected: logging support */

		protected String getLog()
		{
			return this.getClass().getName();
		}

		protected void   logLockFound()
		{
			if(LU.isD(getLog())) LU.D(getLog(),
				  " lock already exists: ", this.lock);
		}

		protected void   logLockCreated()
		{
			if(LU.isD(getLog())) LU.D(getLog(),
			  " created lock: ", this.lock);
		}


		/* protected: action parameters */

		protected final United    owner;
		protected final UnityType type;


		/* protected: the lock */

		protected Lock            lock;
	}
}