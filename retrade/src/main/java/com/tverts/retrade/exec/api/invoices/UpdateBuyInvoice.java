package com.tverts.retrade.exec.api.invoices;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: api */

import com.tverts.api.retrade.document.Buy;


/**
 * Updates Buy Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateBuyInvoice extends UpdateInvoiceBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Buy);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_BUY;
	}
}