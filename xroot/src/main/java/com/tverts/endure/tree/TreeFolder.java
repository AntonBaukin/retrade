package com.tverts.endure.tree;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.Entity;
import com.tverts.endure.cats.CodedEntity;
import com.tverts.endure.cats.NamedEntity;


/**
 * A folder within Tree Domain.
 *
 * @author anton.baukin@gmail.com
 */
public class      TreeFolder
       extends    Entity
       implements CodedEntity, NamedEntity
{
	/* public: TreeFolder (bean) interface */

	public TreeDomain getDomain()
	{
		return domain;
	}

	public void setDomain(TreeDomain domain)
	{
		this.domain = domain;
	}

	public TreeFolder getParent()
	{
		return parent;
	}

	public void setParent(TreeFolder parent)
	{
		this.parent = parent;
	}

	/**
	 * Code unique within Tree Domain.
	 */
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	/* tree domain reference */

	private TreeDomain domain;
	private TreeFolder parent;


	/* folder attributes */

	private String     code;
	private String     name;
}