package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.core.Entity;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.PaySelf;


/**
 * Refers two Self Payments with Payment Ways of
 * Bank (optional) and Cash. Referred by Pos Desks.
 *
 * @author anton.baukin@gmail.com
 */
public class PayDesk extends Entity implements DomainEntity
{
	/* public: PayDesk (bean) interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public PaySelf getPayCash()
	{
		return payCash;
	}

	public void setPayCash(PaySelf payCash)
	{
		this.payCash = payCash;
	}

	public PaySelf getPayBank()
	{
		return payBank;
	}

	public void setPayBank(PaySelf payBank)
	{
		this.payBank = payBank;
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

	public Date getOpenDate()
	{
		return openDate;
	}

	public void setOpenDate(Date openDate)
	{
		this.openDate = openDate;
	}

	public Date getCloseDate()
	{
		return closeDate;
	}

	public void setCloseDate(Date closeDate)
	{
		this.closeDate = closeDate;
	}


	/* attributes & references of payments desk */

	private Domain  domain;
	private PaySelf payCash;
	private PaySelf payBank;
	private String  name;
	private String  remarks;
	private Date    openDate;
	private Date    closeDate;
}