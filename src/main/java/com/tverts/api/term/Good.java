package com.tverts.api.term;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A good unit to sell. Each good unit is a good
 * coupled with the volume unit (mass, volume, etc.).
 * The price of the good means the price of that unit.
 */
@XmlType(name = "good")
public class Good
{
	/**
	 * The primary key of the good unit provided by
	 * the source database.
	 */
	@XmlAttribute(name = "key", required = true)
	public long getGoodKey()
	{
		return goodKey;
	}

	public void setGoodKey(long goodKey)
	{
		this.goodKey = goodKey;
	}

	/**
	 * Tells whether the good is removed from
	 * the catalogue or the price list.
	 */
	@XmlAttribute(name = "removed")
	public Boolean isRemoved()
	{
		return Boolean.TRUE.equals(removed)?(Boolean.TRUE):(null);
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = removed;
	}

	@XmlElement(name = "good-code")
	public String getGoodCode()
	{
		return goodCode;
	}

	public void setGoodCode(String goodCode)
	{
		this.goodCode = goodCode;
	}

	@XmlElement(name = "good-title")
	public String getGoodTitle()
	{
		return goodTitle;
	}

	public void setGoodTitle(String goodTitle)
	{
		this.goodTitle = goodTitle;
	}

	/**
	 * The primary key of the good unit measure
	 * unit provided by the source database.
	 */
	@XmlElement(name = "measure-key")
	public Long getMeasureKey()
	{
		return measureKey;
	}

	public void setMeasureKey(Long measureKey)
	{
		this.measureKey = measureKey;
	}

	@XmlElement(name = "measure-code")
	public String getMeasureCode()
	{
		return measureCode;
	}

	public void setMeasureCode(String measureCode)
	{
		this.measureCode = measureCode;
	}

	@XmlElement(name = "measure-title")
	public String getMeasureTitle()
	{
		return measureTitle;
	}

	public void setMeasureTitle(String measureTitle)
	{
		this.measureTitle = measureTitle;
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


	/* Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		Good good = (Good)o;
		return goodKey == good.goodKey;
	}

	public int hashCode()
	{
		return (int)(goodKey ^ (goodKey >>> 32));
	}


	/* attributes */

	private long       goodKey;
	private Boolean    removed;
	private String     goodCode;
	private String     goodTitle;
	private Long       measureKey;
	private String     measureCode;
	private String     measureTitle;
	private BigDecimal price;
}