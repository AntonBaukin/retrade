package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

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
public class PriceChangeEdit extends PriceChangeView
{
	public static final long serialVersionUID = 0L;


	/* public: PriceChangeEdit (bean) interface */

	public Long    getPriceList()
	{
		return priceList;
	}

	public void    setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	public Long    getGoodUnitInit()
	{
		return goodUnitInit;
	}

	public void    setGoodUnitInit(Long goodUnitInit)
	{
		this.goodUnitInit = goodUnitInit;
	}


	/* public: initialization interface */

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


	/* public: Object interface */

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


	/* private: properties of the view */

	private Long priceList;
	private Long goodUnitInit;
}