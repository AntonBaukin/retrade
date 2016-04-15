package com.tverts.retrade.domain.invoice;

/**
 * Denotes Goods of Invoices supporting
 * altering with the goods calculations.
 *
 * @author anton.baukin@gmail.com
 */
public interface NeedCalcGood
{
	/* public: NeedCalcGood interface */

	public Boolean getNeedCalc();

	public void    setNeedCalc(Boolean nc);
}