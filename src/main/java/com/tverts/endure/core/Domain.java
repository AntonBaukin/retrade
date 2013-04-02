package com.tverts.endure.core;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItem;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Domain defines a database content part that
 * belongs to a client (owning this domain).
 *
 * Data of domains are isolated from each other
 * and do not intercept, including the system
 * objects such as security items (roles,
 * logins rules, and other).
 *
 *
 * @author anton.baukin@gmail.com
 */
public class Domain extends Entity implements CatItem
{
	/* public: Domain bean interface */

	public String  getCode()
	{
		return code;
	}

	public void    setCode(String code)
	{
		this.code   = code;
		this.codeux = null;
	}

	/**
	 * The codes od Domain are case-insensible, and this
	 * lowe-cased variant of code is for uniqueness on
	 * the database level.
	 */
	public String  getCodeux()
	{
		return (codeux != null)?(codeux):
		  (codeux = (code == null)?(null):(code.toLowerCase()));
	}

	public void    setCodeux(String codeux)
	{
		this.codeux = codeux;
	}

	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		if((name = s2s(name)) == null)
			throw new IllegalArgumentException();

		this.name = name;
	}


	/* public: DomainEntity interface */

	public Domain  getDomain()
	{
		return this;
	}

	public void    setDomain(Domain domain)
	{}


	/* private: persistent attributes */

	private String code;
	private String codeux;
	private String name;
}