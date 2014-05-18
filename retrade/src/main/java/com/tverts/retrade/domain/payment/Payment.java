package com.tverts.retrade.domain.payment;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: endure (core + order) */

import com.tverts.endure.UnitedTxBase;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;
import com.tverts.endure.order.OrderIndex;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.PayFirm;
import com.tverts.retrade.domain.account.PaySelf;


/**
 * Payment is a link between {@link PayOrder}
 * and {@link PaySelf}. This means the the
 * Domain account is always a side of a payment.
 *
 * Concrete subclasses extend this link including,
 * for example, {@link PayFirm} as the opposite
 * side of a payment.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class Payment
       extends        UnitedTxBase
       implements     DomainEntity, OrderIndex
{
	/* public: Payment (bean) interface */

	public Domain getDomain()
	{
		if(domain != null)
			return domain;

		return domain = (getPayOrder() != null)?(getPayOrder().getDomain()):
		  (getPaySelf() != null)?(getPaySelf().getDomain()):(null);
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public PayOrder getPayOrder()
	{
		return order;
	}

	public void setPayOrder(PayOrder order)
	{
		this.order = order;
	}

	public PaySelf getPaySelf()
	{
		return paySelf;
	}

	public void setPaySelf(PaySelf paySelf)
	{
		this.paySelf = paySelf;
	}

	public Long getOrderIndex()
	{
		return orderIndex;
	}

	public void setOrderIndex(Long orderIndex)
	{
		this.orderIndex = orderIndex;
	}

	/**
	 * The timestamp when payment was made.
	 */
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	/**
	 * The code of the payment is support information,
	 * it may be not unique within the Domain.
	 */
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

	public BigDecimal getIncome()
	{
		return income;
	}

	public void setIncome(BigDecimal i)
	{
		if((i != null) && (i.scale() != 2))
			i = i.setScale(2);

		this.income = i;
	}

	public BigDecimal getExpense()
	{
		return expense;
	}

	public void setExpense(BigDecimal e)
	{
		if((e != null) && (e.scale() != 2))
			e = e.setScale(2);

		this.expense = e;
	}


	/* public: OrderIndex interface */

	public Unity     getOrderOwner()
	{
		return (getDomain() == null)?(null):(getDomain().getUnity());
	}

	public UnityType getOrderType()
	{
		return null;
	}


	/* order and pay self */

	private Domain       domain;
	private PayOrder     order;
	private PaySelf      paySelf;
	private Long         orderIndex;


	/* info attributes */

	private Date         time;
	private String       code;
	private String       remarks;


	/* payment amount */

	private BigDecimal   income;
	private BigDecimal   expense;
}