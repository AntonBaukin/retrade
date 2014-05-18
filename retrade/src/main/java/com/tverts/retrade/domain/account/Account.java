package com.tverts.retrade.domain.account;

/* com.tverts: endure (core) */

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
 *
 * @author anton.baukin@gmail.com
 */
public class Account extends Entity implements CatItem
{
	/* public: Account (bean) interface */

	public Contractor  getContractor()
	{
		return contractor;
	}

	public void        setContractor(Contractor contractor)
	{
		this.contractor = contractor;
	}

	public Domain      getDomain()
	{
		return domain;
	}

	public void        setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public String      getCode()
	{
		return code;
	}

	public void        setCode(String code)
	{
		this.code = code;
	}

	public String      getName()
	{
		return name;
	}

	public void        setName(String name)
	{
		this.name = name;
	}

	public String      getRemarks()
	{
		return remarks;
	}

	public void        setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* attributes & references */

	private Contractor contractor;
	private Domain     domain;
	private String     code;
	private String     name;
	private String     remarks;
}