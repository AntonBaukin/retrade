package com.tverts.retrade.domain.firm;

/* Java */

import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: endure domain (catalogs + persons) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;
import com.tverts.endure.person.FirmEntity;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Stores properties of a {@link Contractor}
 * and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "contractor")
public class ContractorView extends CatItemView
{
	/* Contractor View Bean */

	@XmlElement
	public String getFullName()
	{
		return fullName;
	}

	private String fullName;

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	@XmlElement
	public BigDecimal getIncome()
	{
		return (income != null)
		  ?(income.setScale(2, BigDecimal.ROUND_HALF_UP))
		  :(new BigDecimal(0).setScale(2));
	}

	private BigDecimal income;

	public void setIncome(BigDecimal v)
	{
		if(v != null) v = v.setScale(5);
		this.income = v;
	}

	@XmlElement
	public BigDecimal getExpense()
	{
		return (expense != null)
		  ?(expense.setScale(2, BigDecimal.ROUND_HALF_UP))
		  :(new BigDecimal(0).setScale(2));
	}

	private BigDecimal expense;

	public void setExpense(BigDecimal v)
	{
		if(v != null) v = v.setScale(5);
		this.expense = v;
	}

	@XmlElement
	public BigDecimal getBalance()
	{
		BigDecimal b = getIncome();

		if((b != null) && (getExpense() != null))
			b = b.subtract(getExpense());
		if(b != null)
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b;
	}


	/* Initialization */

	public ContractorView init(Object obj)
	{
		if(obj instanceof FirmEntity)
			return this.init((FirmEntity) obj);

		if(obj instanceof AggrValue)
			return this.initAggrValue((AggrValue) obj);

		return (ContractorView) super.init(obj);
	}

	public ContractorView init(CatItem ci)
	{
		super.init(ci);

		if(ci instanceof Contractor)
			this.init((Contractor)ci);

		return this;
	}

	public ContractorView init(Contractor c)
	{
		//~: full name of the firm
		if(SU.sXe(fullName) && c.getFirm() != null)
			this.fullName = c.getFirm().getOx().getFullName();

		return this;
	}

	public ContractorView init(FirmEntity f)
	{
		//~: full name of the firm
		this.fullName = f.getOx().getFullName();

		return this;
	}

	public ContractorView initAggrValue(AggrValue av)
	{
		//?: {rest cost value}
		if(Contractors.aggrTypeContractorDebt().equals(av.getAggrType()))
			return initContractorDebt(av);

		return this;
	}

	public ContractorView initContractorDebt(AggrValue cd)
	{
		this.income  = cd.getAggrPositive();
		this.expense = cd.getAggrNegative();

		return this;
	}
}