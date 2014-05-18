package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Creates actions to update DocumentView of
 * Volume Check Documents (implemented as Invoices).
 *
 * @author anton.baukin@gmail.com
 */
public class   ActVolumeCheckUpdateDocumentView
       extends ActInvoiceBuySellUpdateDocumentView
{
	/* protected: ActDocumentViewBase interface */

	protected boolean isThatViewOwner(ActionBuildRec abr)
	{
		Unity u = viewOwner(abr).getUnity();
		return Invoices.isVolumeCheck(u);
	}
}