package com.tverts.retrade.domain.store;

/* com.tverts: endure (core + catalogues)*/

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;


/**
 * Trade Store is a catalogue item naming the stores
 * of the domain. Store may be also a business item
 * related to the production.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TradeStore extends Entity implements CatItem
{
	/* public: TradeStore bean interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void   setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String getCode()
	{
		return code;
	}

	public void   setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void   setName(String name)
	{
		this.name = name;
	}


	/* private: persisted attributes */

	private Domain domain;
	private String code;
	private String name;
}