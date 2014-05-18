package com.tverts.retrade.domain.payment;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Payment Order to (from) some Contractor.
 * {@link InvoiceBill}s do refer this order.
 *
 * @author anton.baukin@gmail.com
 */
public class FirmOrder extends PayOrder
{
	/* public: FirmOrder (bean) interface */

	public Contractor getContractor()
	{
		return contractor;
	}

	public void setContractor(Contractor contractor)
	{
		this.contractor = contractor;
	}


	/* the contractor */

	private Contractor contractor;
}