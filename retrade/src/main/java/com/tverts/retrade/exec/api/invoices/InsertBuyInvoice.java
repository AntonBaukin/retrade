package com.tverts.retrade.exec.api.invoices;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.BuyData;
import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: api */

import com.tverts.api.retrade.document.Buy;


/**
 * Inserts new Buy Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertBuyInvoice extends InsertInvoiceBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Buy);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String      getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_BUY;
	}

	protected InvoiceData createInvoiceData()
	{
		return new BuyData();
	}

	protected InvGood     createGood()
	{
		return new BuyGood();
	}
}