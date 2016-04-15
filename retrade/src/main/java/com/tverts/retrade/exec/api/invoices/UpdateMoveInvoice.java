package com.tverts.retrade.exec.api.invoices;

/* com.tverts: endure (core) */

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.document.Move;
import com.tverts.api.retrade.goods.GoodSell;


/**
 * Updates Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateMoveInvoice extends UpdateInvoiceBase
{
	/* protected: UpdateEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Move);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String  getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_MOVE;
	}

	/**
	 * HINT: move operations don't involve contractors.
	 */
	protected void    assignContractor(InvoiceEdit ie, BuySell bs)
	{}

	protected void    assignStore(InvoiceEdit ie, BuySell bs)
	{
		super.assignStore(ie, bs);

		//~: assign the source store
		ie.setTradeStoreSource(loadStore(
		  ie.objectKey(), ((Move)bs).getSource()).
		  getPrimaryKey());
	}

	/**
	 * HINT: move operations has no costs of the goods.
	 */
	protected void    assignGoodCost(GoodSell gs, InvoiceGoodView ge)
	{}
}