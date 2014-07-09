package com.tverts.retrade.domain.firm;

/* standard Java classes */

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


/**
 * Stores properties of a {@link Contractor}
 * and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "contractor")
public class ContractorView extends CatItemView
{
	public static final long serialVersionUID = 0L;


	/* public: ContractorView (bean) interface */

	public String     getFullName()
	{
		return fullName;
	}

	public void       setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public BigDecimal getIncome()
	{
		return (income != null)
		  ?(income.setScale(2, BigDecimal.ROUND_HALF_UP))
		  :(new BigDecimal(0).setScale(2));
	}

	public void       setIncome(BigDecimal v)
	{
		if(v != null) v = v.setScale(5);
		this.income = v;
	}

	public BigDecimal getExpense()
	{
		return (expense != null)
		  ?(expense.setScale(2, BigDecimal.ROUND_HALF_UP))
		  :(new BigDecimal(0).setScale(2));
	}

	public void       setExpense(BigDecimal v)
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


	/* public: initialization interface */

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


	/* private: properties of the contractor */

	private String fullName;

	/* private: aggregated values */

	private BigDecimal income;
	private BigDecimal expense;
}