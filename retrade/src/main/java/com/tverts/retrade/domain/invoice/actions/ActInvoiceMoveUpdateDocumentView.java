package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Creates actions to update DocumentView of Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class   ActInvoiceMoveUpdateDocumentView
       extends ActInvoiceBuySellUpdateDocumentView
{
	/* protected: ActDocumentViewBase interface */

	protected boolean isThatViewOwner(ActionBuildRec abr)
	{
		Unity u = viewOwner(abr).getUnity();
		return Invoices.isMoveInvoice(u);
	}
}