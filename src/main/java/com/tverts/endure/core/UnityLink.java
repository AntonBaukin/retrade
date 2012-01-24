package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.UnitingLink;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/**
 * COMMENT UnityLink
 *
 * @author anton.baukin@gmail.com
 */
public class UnityLink implements United, UnitingLink
{
	/* public: PrimaryIdentity interface */

	public Long      getPrimaryKey()
	{
		return primaryKey;
	}

	public void      setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: United interface */

	public Unity     getUnity()
	{
		return unity;
	}

	public void      setUnity(Unity unity)
	{
		this.unity = unity;

		//!: set the link type to be the same
		if(unity != null)
			setLinkType(unity.getUnityType());
	}


	/* public: UnitingLink interface */

	public UnityType getLinkType()
	{
		return linkType;
	}

	public void      setLinkType(UnityType linkType)
	{
		if(linkType == null)
			throw new  IllegalArgumentException();

		this.linkType = linkType;
	}

	public Unity     getOwner()
	{
		return owner;
	}

	public void      setOwner(Unity owner)
	{
		if(owner == null)
			throw new IllegalArgumentException();

		this.owner = owner;
	}

	public Unity     getLinked()
	{
		return linked;
	}

	public void      setLinked(Unity linked)
	{
		if(linked == null)
			throw new IllegalArgumentException();

		this.linked = linked;
	}

	public long      getLinkGroup()
	{
		return (linkGroup != 0L)?(linkGroup):
		  (primaryKey != null)?(primaryKey):(0L);
	}

	public void      setLinkGroup(long linkGroup)
	{
		if(linkGroup == 0L)
			throw new IllegalArgumentException();

		this.linkGroup = linkGroup;
	}

	public long      getLinkHelper()
	{
		return linkHelper;
	}

	public void      setLinkHelper(long linkHelper)
	{
		this.linkHelper = linkHelper;
	}


	/* public: Object interface */

	public boolean equals(Object l)
	{
		if(this == l)
			return true;

		if(!(l instanceof UnityLink))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((UnityLink)l).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int     hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}


	/* private: persistent attributes */

	private Long      primaryKey;

	private Unity     unity;
	private UnityType linkType;
	private Unity     owner;
	private Unity     linked;

	private long      linkGroup;
	private long      linkHelper;
}