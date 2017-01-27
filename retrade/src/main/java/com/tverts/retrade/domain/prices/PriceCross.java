package com.tverts.retrade.domain.prices;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (firms + goods) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.goods.GoodUnit;


/**
 * This link entity is to rapidly connect
 * a {@link Contractor} with the prices
 * {@link GoodPrice} of {@link GoodUnit}.
 *
 * Introduction of {@link FirmPrices} as
 * a priority ordered set of Price Lists
 * significantly increased the complexity
 * of accessing the prices. To solve this
 * problem, the cross link is involved.
 *
 * WARNING! This entities are not to load,
 * but to use as links only when selecting
 * the good prices!
 *
 *
 * @author anton.baukin@gmail.com
 */
public class PriceCross extends NumericBase
{
	public FirmPrices getFirmPrices()
	{
		return firmPrices;
	}

	private FirmPrices firmPrices;

	public void setFirmPrices(FirmPrices firmPrices)
	{
		this.firmPrices = firmPrices;
	}

	public GoodPrice getGoodPrice()
	{
		return goodPrice;
	}

	private GoodPrice goodPrice;

	public void setGoodPrice(GoodPrice goodPrice)
	{
		this.goodPrice = goodPrice;
	}

	public Contractor getContractor()
	{
		return contractor;
	}

	private Contractor contractor;

	public void setContractor(Contractor contractor)
	{
		this.contractor = contractor;
	}

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	private GoodUnit goodUnit;

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}
}