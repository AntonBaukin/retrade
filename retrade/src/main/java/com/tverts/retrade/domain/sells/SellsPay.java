package com.tverts.retrade.domain.sells;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.PayOrder;
import com.tverts.retrade.domain.payment.Payment;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Variant of {@link Payment} exclusively for
 * {@link SellsSession} Payment Orders.
 *
 * @author anton.baukin@gmail.com
 */
public class SellsPay extends Payment
{
	/* public: Payment (bean) interface */

	public void setPayOrder(PayOrder order)
	{
		EX.assertx((order == null) ||
		  SellsSession.class.isAssignableFrom(HiberPoint.type(order))
		);

		super.setPayOrder(order);
	}
}