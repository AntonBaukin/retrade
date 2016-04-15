package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.UnitedTxBase;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * Unity view is abstraction of an object
 * that aggregates the attributes and state
 * of the related {@link Unity} instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class UnityView
       extends        UnitedTxBase
{
	/* public: UnityView (bean) interface */

	public Domain     getDomain()
	{
		return domain;
	}

	public void       setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public Unity      getViewOwner()
	{
		return viewOwner;
	}

	public void       setViewOwner(Unity viewOwner)
	{
		this.viewOwner = viewOwner;
	}

	/**
	 * The type of the owner. Note that type of entities
	 * changes rarely. The one notable except is states.
	 */
	public UnityType  getOwnerType()
	{
		return (ownerType != null)?(ownerType):
		  (getViewOwner() == null)?(null):(getViewOwner().getUnityType());
	}

	public void       setOwnerType(UnityType ownerType)
	{
		this.ownerType = ownerType;
	}



	/**
	 * The type of the Unity state. (Only for entities
	 * having states.)
	 */
	public UnityType  getOwnerState()
	{
		return ownerState;
	}

	public void       setOwnerState(UnityType ownerState)
	{
		this.ownerState = ownerState;
	}


	/* persisted attributes & links */

	private Domain    domain;
	private Unity     viewOwner;
	private UnityType ownerType;
	private UnityType ownerState;
}