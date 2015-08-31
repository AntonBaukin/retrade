package com.tverts.retrade.domain.invoice;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Plain persisted entity of Invoice Good.
 *
 * @author anton.baukin@gmail.com
 */
public class VolGood extends InvGood
{
	/* public: InvGood (bean) interface */

	public VolData getData()
	{
		return (VolData) super.getData();
	}

	public void setData(InvoiceData data)
	{
		EX.assertx((data == null) || (data instanceof VolData));
		super.setData(data);
	}
}