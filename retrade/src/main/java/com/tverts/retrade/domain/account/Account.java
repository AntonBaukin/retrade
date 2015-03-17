package com.tverts.retrade.domain.account;

/* com.tverts: endure (core) */

import com.tverts.endure.Remarkable;
import com.tverts.endure.cats.CatItem;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.Entity;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Balance account entity. May be assigned to
 * a Contractor, or to be own account (having
 * the contractor undefined).
 *
 * @author anton.baukin@gmail.com
 */
public class      Account
       extends    Entity
       implements CatItem, Remarkable
{
	/* Account */

	public Domain getDomain()
	{
		return domain;
	}

	private Domain domain;

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public Contractor getContractor()
	{
		return contractor;
	}

	private Contractor contractor;

	public void setContractor(Contractor contractor)
	{
		this.contractor = contractor;
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRemarks()
	{
		return remarks;
	}

	private String remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}
}