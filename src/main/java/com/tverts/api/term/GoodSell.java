package com.tverts.api.term;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An item of a receipt. Tells what good
 * is sold, and at what amount (volume)
 * and the price.
 */
@XmlType(name = "good-sell")
public class GoodSell
{
	@XmlAttribute(name = "good-key", required = true)
	public long getGood()
	{
		return good;
	}

	public void setGood(long good)
	{
		this.good = good;
	}

	/**
	 * The primary key of the price list when the good
	 * is sold from it.
	 */
	@XmlAttribute(name = "price-list-key")
	public Long getList()
	{
		return list;
	}

	public void setList(Long list)
	{
		this.list = list;
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
	@XmlElement(name = "cost", required = true)
	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCost(BigDecimal cost)
	{
		this.cost = cost;
	}

	/**
	 * A price of one unit of the good.
	 * May be set directly or taken from the price list.
	 */
	@XmlElement(name = "unit-price", required = true)
	public BigDecimal getUnitPrice()
	{
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice)
	{
		this.unitPrice = unitPrice;
	}


	/* attributes */

	private long       good;
	private Long       list;
	private BigDecimal volume;
	private BigDecimal cost;
	private BigDecimal unitPrice;
}