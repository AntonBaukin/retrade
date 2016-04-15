package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Action builder to update Volume Check Document
 * implemented as an Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class ActVolumeCheckUpdate extends ActInvoiceBuySellUpdate
{

	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_VOLUME_CHECK.equals(tname);
	}


	/* protected: update action methods */

	protected void updateInvoiceBill(ActionBuildRec abr)
	{
		//HINT: Volume Check Document has no Bill.
	}
}