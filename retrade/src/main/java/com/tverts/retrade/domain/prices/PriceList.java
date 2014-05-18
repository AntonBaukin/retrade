package com.tverts.retrade.domain.prices;

/* tverts.com: endure (core + catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;


/**
 * Collection of prices for the goods.
 *
 * @author anton.baukin@gmail.com
 */
public class PriceList extends Entity implements CatItem
{
	/* public: PriceList (bean) interface */

	public Domain    getDomain()
	{
		return domain;
	}

	public void      setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String    getCode()
	{
		return code;
	}

	public void      setCode(String code)
	{
		this.code = code;
	}

	public String    getName()
	{
		return name;
	}

	public void      setName(String name)
	{
		this.name = name;
	}

	public PriceList getParent()
	{
		return parent;
	}

	public void      setParent(PriceList parent)
	{
		this.parent = parent;
	}


	/* persisted attributes */

	private Domain    domain;
	private String    code;
	private String    name;
	private PriceList parent;
}