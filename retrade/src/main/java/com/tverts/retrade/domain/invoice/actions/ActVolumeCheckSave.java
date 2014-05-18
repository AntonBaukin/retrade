package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Action builder to save Volume Check
 * Documents implemented as Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class ActVolumeCheckSave extends ActInvoiceBuySellSave
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_VOLUME_CHECK.equals(tname);
	}

	/**
	 * Volume Check Documents have no Contractor and are not billed.
	 */
	protected void    saveInvoiceBill(ActionBuildRec abr)
	{}
}