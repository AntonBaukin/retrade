package com.tverts.endure.locks;

/* Hibernate Persistence Layer */

import com.tverts.endure.NumericIdentity;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: endure core */

import com.tverts.endure.PrimaryIdentity;
import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * This helper class is for obtaining {@link Lock}s.
 *
 * A lock is set via 'select for update' method.
 * A lock obtained may be cleared only after
 * ending the transaction of the session.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Locks
{
	/* logging */

	public static final String LOG =
	  Lock.class.getName();


	/* public: obtaining locks */

	/**
	 * Sets the lock with 'no wait' option. Raises exception
	 * when the lock is being held by concurrent transaction.
	 *
	 * Additional lock requests on the same lock instance within
	 * the same transaction have no effect.
	 */
	public static void lock(NumericIdentity lock, Session session)
	{
		if(session == null)
			session = TxPoint.txSession();

		//!: do lock nowait
		session.buildLockRequest(LockOptions.UPGRADE).
		  setLockMode(LockMode.UPGRADE_NOWAIT).
		  lock(lock);

		LU.D(LOG, "on ", LU.sig(lock));
	}

	public static void lock(NumericIdentity lock)
	{
		lock(lock, null);
	}

	/**
	 * Searches for {@link Lock} instance and obtains it.
	 */
	public static Lock lock(Long owner, UnityType type, Session session)
	{
		if(session == null)
			session = TxPoint.txSession();

		GetLock get = bean(GetLock.class);
		get.setSession(session);

		Lock    lck = get.getLockStrict(owner, type);

		//!: set the lock
		lock(lck, session);

		return lck;
	}

	public static Lock lock(PrimaryIdentity owner, String type)
	{
		return lock(owner.getPrimaryKey(),
		  UnityTypes.unityType(Lock.class, type), null);
	}
}