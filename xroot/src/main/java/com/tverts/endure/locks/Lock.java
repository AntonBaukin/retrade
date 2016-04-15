package com.tverts.endure.locks;

/* com.tverts: endure core */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: support */

import static com.tverts.support.LU.sig;


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

	public Long      getPrimaryKey()
	{
		return primaryKey;
	}

	public void      setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public Unity     getOwner()
	{
		return owner;
	}

	public void      setOwner(Unity owner)
	{
		this.owner = owner;
	}

	public UnityType getLockType()
	{
		return lockType;
	}

	public void      setLockType(UnityType lockType)
	{
		this.lockType = lockType;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(!(o instanceof Lock))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((Lock)o).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int     hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}
	
	public String  toString()
	{
		return String.format(
		  "Lock %s owned by [%s]",
		  (getLockType() == null)?("undefined"):(getLockType().toString()),
		  sig(getOwner())
		);
	}


	/* persisted attributes */
	
	private Long      primaryKey;
	private Unity     owner;
	private UnityType lockType;
}