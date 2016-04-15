package com.tverts.retrade.domain.sells;

/* com.tverts: endure (core + catalogues) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;
import com.tverts.endure.cats.CatItem;


/**
 * Sells terminal.
 *
 * @author anton.baukin@gmail.com
 */
public class SellsDesk extends Entity implements CatItem
{
	/* public: SellsDesk (bean) interface */

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

	public PayDesk getPayDesk()
	{
		return payDesk;
	}

	public void setPayDesk(PayDesk payDesk)
	{
		this.payDesk = payDesk;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* attributes & references of sells terminal */

	private Domain  domain;
	private String  code;
	private PayDesk payDesk;
	private String  name;
	private String  remarks;
}