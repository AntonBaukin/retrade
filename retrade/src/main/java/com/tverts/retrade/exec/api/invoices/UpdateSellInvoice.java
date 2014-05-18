package com.tverts.retrade.exec.api.invoices;

/* com.tverts: retrade domain (invoices) */


import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.document.Sell;
import com.tverts.api.retrade.goods.GoodSell;


/**
 * Updates Sell Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateSellInvoice extends UpdateInvoiceBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Sell);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String  getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_SELL;
	}

	protected void    assignPriceList
	  (GoodSell gs, InvoiceGoodView ge, InvoiceEdit ie)
	{
		if(gs.getList() == null)
			ge.setPriceList(null);
		else
			ge.setPriceList(loadPriceList(
			  ie.objectKey(), gs.getList()).
			  getPrimaryKey()
			);
	}
}