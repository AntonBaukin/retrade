package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.CMP;


/**
 * Extends read-only view of {@link PriceChange}
 * instance to store edit state.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-change")
@XmlType(name = "price-change-edit")
public class PriceChangeEdit extends PriceChangeView
{
	public static final long serialVersionUID = 20150614L;


	/* Price Change Edit */

	public Long getPriceList()
	{
		return priceList;
	}

	private Long priceList;

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	public Long getGoodUnitInit()
	{
		return goodUnitInit;
	}

	private Long goodUnitInit;

	public void setGoodUnitInit(Long goodUnitInit)
	{
		this.goodUnitInit = goodUnitInit;
	}

	public boolean isFixPrice()
	{
		return fixPrice;
	}

	private boolean fixPrice;

	public void setFixPrice(boolean fixPrice)
	{
		this.fixPrice = fixPrice;
	}


	/* Initialization */

	public PriceChangeEdit init(Object obj)
	{
		return (PriceChangeEdit)super.init(obj);
	}

	public PriceChangeEdit init(GoodUnit gu)
	{
		//~: initial good key
		this.goodUnitInit = gu.getPrimaryKey();

		return (PriceChangeEdit) super.init(gu);
	}


	/* Object */

	public boolean equals(Object o)
	{
		if((this == o) || (o == null) || !getClass().equals(o.getClass()))
			return (this == o);

		//!: compare by the codes of the goods
		return CMP.eq(getGoodCode(), ((PriceChangeEdit)o).getGoodCode());
	}

	public int     hashCode()
	{
		return (getGoodCode() == null)?(0):(getGoodCode().hashCode());
	}
}