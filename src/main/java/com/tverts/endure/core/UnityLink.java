package com.tverts.endure.core;

/* com.tverts: endure */

import com.tverts.endure.United;
import com.tverts.endure.UnitedBase;
import com.tverts.endure.UnitingLink;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * General link between two {@link United}
 * instances (having unified mirrors).
 *
 * @author anton.baukin@gmail.com
 */
public class      UnityLink
       extends    UnitedBase
       implements UnitingLink
{
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
		  (getPrimaryKey() != null)?(getPrimaryKey()):(0L);
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


	/* persistent attributes & links */

	private UnityType linkType;
	private Unity     owner;
	private Unity     linked;

	private long      linkGroup;
	private long      linkHelper;
}