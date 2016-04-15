package com.tverts.retrade.domain.prices;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Connects a {@link Contractor} with
 * a {@link PriceListEntity}.
 *
 * Each Contractor may be linked with
 * several Price Lists. The Good Unit
 * price is selected based on this
 * entity priority index: price
 * from the highest index wins.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class FirmPrices extends NumericBase
{
	public PriceListEntity getPriceList()
	{
		return priceList;
	}

	private PriceListEntity priceList;

	public void setPriceList(PriceListEntity priceList)
	{
		this.priceList = priceList;
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

	/**
	 * The priority of the related Price Lists within the
	 * lists assigned to the Contractor. The Good Unit price
	 * is selected by the highest priority value.
	 */
	public int getPriority()
	{
		return priority;
	}

	private int priority;

	public void setPriority(int priority)
	{
		this.priority = priority;
	}
}