package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;
import com.tverts.api.core.XKeysAlias;
import com.tverts.api.retrade.prices.PriceItem;


/**
 * An item of a receipt. Tells what good
 * is sold, and at what amount (volume)
 * and the price.
 */
@XKeysAlias(Good.class)
@XmlType(name = "good-sell", propOrder = {
  "store", "XStore", "volume", "cost", "payFlag"
})
public class GoodSell extends PriceItem
{
	public static final long serialVersionUID = 0L;

	@XKeyPair(type = Store.class)
	@XmlElement(name = "store")
	public Long getStore()
	{
		return (store == 0L)?(null):(store);
	}

	public void setStore(Long store)
	{
		this.store = (store == null)?(0L):(store);
	}

	@XmlElement(name = "xstore")
	public String getXStore()
	{
		return xstore;
	}

	public void setXStore(String xstore)
	{
		this.xstore = xstore;
	}

	/**
	 * The volume of the good measured in
	 * it's (good's) units.
	 */
	@XmlElement(name = "volume")
	public BigDecimal getVolume()
	{
		return volume;
	}

	public void setVolume(BigDecimal volume)
	{
		this.volume = volume;
	}

	/**
	 * The cost of the whole volume sold.
	 */
	@XmlElement(name = "cost")
	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCost(BigDecimal cost)
	{
		this.cost = cost;
	}

	/**
	 * Denotes the method of payment:
	 *  · "B"  for bank payment;
	 *  · "C"  for cash payment.
	 */
	@XmlElement(name = "pay-flag")
	public String getPayFlag()
	{
		return payFlag;
	}

	public void setPayFlag(String f)
	{
		if((f != null) && !"C".equals(f) && !"B".equals(f))
			throw new IllegalArgumentException();
		this.payFlag = f;
	}


	/* attributes */

	private long       store;
	private String     xstore;
	private BigDecimal volume;
	private BigDecimal cost;
	private String     payFlag;
}