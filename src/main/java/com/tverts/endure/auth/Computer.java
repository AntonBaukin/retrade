package com.tverts.endure.auth;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;


/**
 * As the application is not for humans only,
 * external computer systems cooperating do
 * login as a Computers.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Computer extends Entity implements CatItem
{
	/* public: Computer (bean) properties */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

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

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}


	/* persisted attributes */

	private Domain domain;
	private String code;
	private String name;
	private String comment;
}
