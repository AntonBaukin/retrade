package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Action builder to save Move {@link Invoice}s in
 * {@link InvoiceState} Edit state.
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceMoveSave extends ActInvoiceBuySellSave
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_INVOICE_MOVE.equals(tname);
	}

	/**
	 * Move Invoices have no Contractor and are not billed.
	 */
	protected void    saveInvoiceBill(ActionBuildRec abr)
	{}

	protected void    prepareInvoiceData(ActionBuildRec abr)
	{
		//~: update move invoice results
		chain(abr).first(new UpdateMoveInvoiceResults(task(abr)));
	}
}