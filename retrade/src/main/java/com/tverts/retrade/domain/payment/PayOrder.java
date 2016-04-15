package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.UnitedTxBase;
import com.tverts.endure.cats.CodedEntity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Defines abstract Payment Order.
 * Payments do refer the Order they are for.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class PayOrder
       extends        UnitedTxBase
       implements     DomainEntity, CodedEntity
{
	/* public: PayOrder (bean) interface */

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/**
	 * Total (expected) amount of money the Domain
	 * would get to it's accounts.
	 */
	public BigDecimal getTotalIncome()
	{
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal i)
	{
		if((i != null) && (i.scale() != 10))
			i = i.setScale(10);

		this.totalIncome = i;
	}

	/**
	 * Total (expected) amount of money the Domain
	 * would pay from it's accounts.
	 */
	public BigDecimal getTotalExpense()
	{
		return totalExpense;
	}

	public void setTotalExpense(BigDecimal e)
	{
		if((e != null) && (e.scale() != 10))
			e = e.setScale(10);

		this.totalExpense = e;
	}

	/**
	 * Actual amount of money the Domain
	 * got to it's accounts.
	 */
	public BigDecimal getActualIncome()
	{
		return actualIncome;
	}

	public void setActualIncome(BigDecimal i)
	{
		if((i != null) && (i.scale() != 2))
			i = i.setScale(2);

		this.actualIncome = i;
	}

	/**
	 * Actual amount of money the Domain
	 * payed from it's accounts.
	 */
	public BigDecimal getActualExpense()
	{
		return actualExpense;
	}

	public void setActualExpense(BigDecimal e)
	{
		if((e != null) && (e.scale() != 2))
			e = e.setScale(2);

		this.actualExpense = e;
	}


	/* payment attributes */

	private Domain     domain;
	private Date       time;
	private String     code;
	private String     remarks;
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;
	private BigDecimal actualIncome;
	private BigDecimal actualExpense;
}