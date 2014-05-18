package com.tverts.retrade.domain.payment;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.PayFirm;


/**
 * Variant of {@link Payment} where the opposite
 * side is a Contractor with it's {@link PayFirm}.
 *
 * @author anton.baukin@gmail.com
 */
public class Settling extends Payment
{
	/* public: Settling (bean) interface */

	public PayFirm getPayFirm()
	{
		return payFirm;
	}

	public void setPayFirm(PayFirm payFirm)
	{
		this.payFirm = payFirm;
	}


	/* pay firm */

	private PayFirm payFirm;
}