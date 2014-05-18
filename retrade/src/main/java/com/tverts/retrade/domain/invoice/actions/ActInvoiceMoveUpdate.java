package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Action builder to update Move Invoices
 * being in InvoiceState Edit state.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   ActInvoiceMoveUpdate
       extends ActInvoiceBuySellUpdate
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_INVOICE_MOVE.equals(tname);
	}


	/* protected: update action methods */

	/**
	 * Move Invoices have no Contractor and are not billed.
	 */
	protected void    updateInvoiceBill(ActionBuildRec abr)
	{}


	/* protected: save action methods */

	protected void    prepareInvoiceData(ActionBuildRec abr)
	{
		//~: update move invoice results
		chain(abr).first(new UpdateMoveInvoiceResults(task(abr)));
	}
}