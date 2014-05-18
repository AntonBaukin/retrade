package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.UnitedTxBase;
import com.tverts.endure.core.DomainEntity;


/**
 * Payment Way defines the actual method of money
 * payment (with it's joined-subclasses).
 *
 * Not depending on what account the payment way
 * is attached to, positive money transfer means
 * the money paid to the domain, negative means
 * paid by the domain. This reduces the mess with
 * credit-debit operations on active, passive, and
 * active+passive accounts.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class PayWay
       extends        UnitedTxBase
       implements     DomainEntity
{
	/* public: PayWay (bean) interface */

	public Domain  getDomain()
	{
		return domain;
	}

	public void    setDomain(Domain domain)
	{
		this.domain = domain;
	}

	/**
	 * Descriptive name of the payment destination.
	 * Not restricted to unique within the account.
	 */
	public String  getName()
	{
		return name;
	}

	public void    setName(String name)
	{
		this.name = name;
	}

	public Date    getOpened()
	{
		return opened;
	}

	public void    setOpened(Date opened)
	{
		this.opened = opened;
	}

	public Date    getClosed()
	{
		return closed;
	}

	public void    setClosed(Date closed)
	{
		this.closed = closed;
	}

	public String  getRemarks()
	{
		return remarks;
	}

	public void    setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/**
	 * The type of this payment way:
	 *
	 *  · U  means undefined (or unified);
	 *  · E  means expense-only;
	 *  · I  means income-only.
	 *
	 * If 'E' flag set, domain may only pay via
	 * this way, but can't be payed from it.
	 * (Credit payments for active accounts.)
	 *
	 * If 'I' flag set, domain may get the money
	 * from this way, but can't pay via it.
	 * (Debit payments for active accounts.)
	 */
	public char    getTypeFlag()
	{
		return typeFlag;
	}

	public void    setTypeFlag(char typeFlag)
	{
		this.typeFlag = typeFlag;
	}


	/* attributes */

	private Domain domain;
	private String name;
	private Date   opened;
	private Date   closed;
	private String remarks;
	private char   typeFlag = 'U';
}