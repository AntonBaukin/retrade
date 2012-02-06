package com.tverts.endure.core;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * COMMENT UnityView
 *
 * @author anton.baukin@gmail.com
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class UnityView implements United
{
	/* public: NumericIdentity interface */

	public Long       getPrimaryKey()
	{
		return primaryKey;
	}

	public void       setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: United interface */

	public Unity      getUnity()
	{
		return unity;
	}

	public void       setUnity(Unity unity)
	{
		this.unity = unity;
	}


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


	/* public: Object interface */

	public boolean    equals(Object u)
	{
		if(this == u)
			return true;

		if(!(u instanceof UnityView))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((UnityView)u).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int        hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}


	/* persisted attributes */

	private Long      primaryKey;
	private Unity     unity;

	private Domain    domain;
	private Unity     viewOwner;
	private UnityType ownerType;
	private UnityType ownerState;
}