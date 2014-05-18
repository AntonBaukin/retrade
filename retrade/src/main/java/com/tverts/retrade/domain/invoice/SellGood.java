package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.prices.GoodPrice;


/**
 * Good of a Sell Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class      SellGood
       extends    InvGood
       implements NeedCalcGood
{
	/* public: bean interface */

	/**
	 * The cost of the whole volume.
	 */
	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCost(BigDecimal c)
	{
		if((c != null) && (c.scale() != 10))
			c = c.setScale(10);

		this.cost = c;
	}

	public GoodPrice getPrice()
	{
		return price;
	}

	public void setPrice(GoodPrice price)
	{
		this.price = price;
	}

	/**
	 * Need-calc flag in Sell Invoice' Good tells whether this
	 * sell position must be calculated by the Good Unit'
	 * Calculation formula if such does present.
	 */
	public Boolean getNeedCalc()
	{
		return needCalc;
	}

	public void setNeedCalc(Boolean nc)
	{
		this.needCalc = nc;
	}


	/* public: InvGood (bean) interface */

	public SellData getData()
	{
		return (SellData) super.getData();
	}

	public void setData(SellData data)
	{
		super.setData(data);
	}


	/* persisted attributes */

	private BigDecimal cost;
	private GoodPrice  price;
	private Boolean    needCalc;
}