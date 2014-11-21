package com.tverts.api.retrade.prices;

/* Java */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.JustTxObject;
import com.tverts.api.core.RemovableObject;
import com.tverts.api.core.XKeyPair;

/* com.tverts: retrade api */

import com.tverts.api.retrade.goods.Good;


/**
 * Combination of a Good Unit reference
 * with it's price. This class itself
 * is for a position of a Price List.
 */
@XmlRootElement(name = "good-price")
@XmlType(name = "good-price", propOrder = {
  "removed", "good", "XGood", "price"
})
public class GoodPrice extends JustTxObject implements RemovableObject
{
	@XmlAttribute
	public Boolean getRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	private boolean removed;

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}

	@XKeyPair(type = Good.class)
	@XmlElement
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	private long good;

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	private String xgood;

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	/**
	 * The price of one unit of the good.
	 */
	@XmlElement
	public BigDecimal getPrice()
	{
		return price;
	}

	private BigDecimal price;

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}
}