package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * TODO comment UnityView
 *
 * @author anton.baukin@gmail.com
 */
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
	 * The type of the view. It is allowed the same owner
	 * to have several views of the same type. It is the
	 * task of the implementation to distinguish them.
	 *
	 * If this view has unity, the view type is allowed
	 * to differ from the unity' one.
	 */
	public UnityType  getViewType()
	{
		return viewType;
	}

	public void       setViewType(UnityType viewType)
	{
		this.viewType = viewType;
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


	/* persisted attributes */

	private Long      primaryKey;
	private Unity     unity;

	private Domain    domain;
	private Unity     viewOwner;
	private UnityType viewType;
	private UnityType ownerType;
}