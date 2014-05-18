package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodCalc;


/**
 * Resulting Good of an altered Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class ResGood extends InvGood
{
	/* public: bean interface */

	/**
	 * Returns the Calculation used to produce this
	 * transitive Invoice Good position.
	 *
	 * If the reference is undefined, the Good volume
	 * of this result is taken from the Invoice' Store
	 * updating the aggregated values.
	 *
	 * Calculated position have special treatment:
	 * the volumes are taken and returned simultaneously
	 * (Store Good have both positive and negative volumes
	 * set) to balance the exiting volume, and to show the
	 * volume transitions in the aggregated values.
	 */
	public GoodCalc getGoodCalc()
	{
		return goodCalc;
	}

	public void setGoodCalc(GoodCalc goodCalc)
	{
		this.goodCalc = goodCalc;
	}


	/* persisted attributes */

	private GoodCalc goodCalc;
}