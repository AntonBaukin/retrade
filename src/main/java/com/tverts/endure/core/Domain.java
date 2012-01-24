package com.tverts.endure.core;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * COMMENT Domain
 *
 * @author anton.baukin@gmail.com
 */
public class      Domain
       extends    Entity
       implements DomainEntity
{
	/* public: Domain bean interface */

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		if((name = s2s(name)) == null)
			throw new IllegalArgumentException();

		this.name = name;
	}


	/* public: DomainEntity interface */

	public Domain getDomain()
	{
		return this;
	}


	/* private: persistent attributes */

	private String name;
}