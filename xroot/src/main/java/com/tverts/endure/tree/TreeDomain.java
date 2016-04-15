package com.tverts.endure.tree;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;


/**
 * Tree Entity is a domain of hierarchy.
 * It consist of folders, references and
 * cross-references.
 *
 * Trees are distinguished in the application
 * by their Domain-unique Unity Types and
 * optional related Unity instance.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      TreeDomain
       extends    Entity
       implements DomainEntity
{
	/*  Tree Domain */

	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public UnityType getTreeType()
	{
		return treeType;
	}

	private UnityType treeType;

	public void setTreeType(UnityType type)
	{
		if((getUnity() != null) && (type != null))
			if(!type.equals(getUnity().getUnityType()))
				getUnity().setUnityType(type);
		this.treeType = type;
	}

	/**
	 * Optional entity this Tree Domain is attached to.
	 */
	public Unity getOwner()
	{
		return owner;
	}

	private Unity owner;

	public void setOwner(Unity owner)
	{
		this.owner = owner;
	}


	/* United Base */

	public void setUnity(Unity unity)
	{
		super.setUnity(unity);

		if((unity != null) && (unity.getUnityType() != null))
			if(!unity.getUnityType().equals(treeType))
				this.treeType = unity.getUnityType();
	}
}