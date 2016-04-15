package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItem;
import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * View on an {@link Account}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "account-view")
public class AccountView extends CatItemView
{
	public static final long serialVersionUID = 20150317L;


	/* Account View */

	public Long getContractorKey()
	{
		return contractorKey;
	}

	private Long contractorKey;

	public void setContractorKey(Long contractorKey)
	{
		this.contractorKey = contractorKey;
	}

	public String getContractorCode()
	{
		return contractorCode;
	}

	private String contractorCode;

	public void setContractorCode(String contractorCode)
	{
		this.contractorCode = contractorCode;
	}

	public String getContractorName()
	{
		return contractorName;
	}

	private String contractorName;

	public void setContractorName(String contractorName)
	{
		this.contractorName = contractorName;
	}

	public BigDecimal getIncome()
	{
		return income;
	}

	private BigDecimal income;

	public void setIncome(BigDecimal v)
	{
		if(v != null) v = v.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.income = v;
	}

	public BigDecimal getExpense()
	{
		return expense;
	}

	private BigDecimal expense;

	public void setExpense(BigDecimal v)
	{
		if(v != null) v = v.setScale(2, BigDecimal.ROUND_HALF_EVEN);
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

	public AccountView init(Object obj)
	{
		if(obj instanceof Account)
			this.init((Account)obj);

		if(obj instanceof Contractor)
			this.init((Contractor)obj);

		return (AccountView) super.init(obj);
	}

	public AccountView init(Contractor c)
	{
		this.contractorKey  = c.getPrimaryKey();
		this.contractorCode = c.getCode();
		this.contractorName = c.getName();

		return this;
	}

	public AccountView initIncome(BigDecimal v)
	{
		setIncome(v);
		return this;
	}

	public AccountView initExpense(BigDecimal v)
	{
		setExpense(v);
		return this;
	}
}