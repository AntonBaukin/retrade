package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;


/**
 * Abstract implementation of a Good Unit is
 * being in an Invoice. Invoice Goods have
 * union hierarchy.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class InvGood extends NumericBase
{
	/* public: bean interface */

	/**
	 * The volume of the Good Unit in the Invoice.
	 * Must be always defined.
	 */
	public BigDecimal getVolume()
	{
		return volume;
	}

	public void setVolume(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.volume = v;
	}

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public InvoiceData getData()
	{
		return data;
	}

	public void setData(InvoiceData data)
	{
		this.data = data;
	}


	/* persisted attributes */

	private BigDecimal  volume;
	private GoodUnit    goodUnit;
	private InvoiceData data;
}