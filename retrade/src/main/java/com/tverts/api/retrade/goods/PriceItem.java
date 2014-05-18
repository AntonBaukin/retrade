package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;


/**
 * Position of a Good Unit sell price in a Price List.
 */
@XmlType(name = "price-item", propOrder = {
  "list", "XList", "good", "XGood", "price"
})
public class PriceItem extends Good
{
	public static final long serialVersionUID = 0L;


	/**
	 * The primary key of the price list that
	 * is assigned when the good price is selected
	 * based on that price list.
	 */
	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "price-list")
	public Long getList()
	{
		return (list == 0L)?(null):(list);
	}

	public void setList(Long list)
	{
		this.list = (list == null)?(0L):(list);
	}

	@XmlElement(name = "xprice-list")
	public String getXList()
	{
		return xlist;
	}

	public void setXList(String xlist)
	{
		this.xlist = xlist;
	}

	@XKeyPair(type = Good.class)
	@XmlElement(name = "good")
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	/**
	 * The price of one unit of the good.
	 */
	@XmlElement(name = "price")
	public BigDecimal getPrice()
	{
		return price;
	}

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}


	/* attributes */

	private long       list;
	private String     xlist;
	private long       good;
	private String     xgood;
	private BigDecimal price;
}