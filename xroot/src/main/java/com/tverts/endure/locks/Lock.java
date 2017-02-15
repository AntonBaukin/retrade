package com.tverts.endure.locks;

/* com.tverts: endure core */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: support */

import com.tverts.support.OU;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Locks are objects attached to the lock owner
 * by the type given.
 *
 * A pair of (lock, type) is unique. For the owner
 * given locks may be selected by the type only.
 * Select proper owner class to detect all other
 * search attributes required.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Lock implements NumericIdentity
{
	/* public: Lock (bean) interface */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	private Long primaryKey;

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Unity getOwner()
	{
		return owner;
	}

	private Unity owner;

	public void setOwner(Unity owner)
	{
		this.owner = owner;
	}

	public UnityType getLockType()
	{
		return lockType;
	}

	private UnityType lockType;

	public void setLockType(UnityType lockType)
	{
		this.lockType = lockType;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		return OU.eq(this, o);
	}

	public int     hashCode()
	{
		return (primaryKey == null)?(0):(primaryKey.hashCode());
	}

	public String  toString()
	{
		return SU.cats("Lock ",
		  (getLockType() == null)?("Undefined"):getLockType(),
		  "owned by [", LU.sig(getOwner()), "]"
		);
	}
}