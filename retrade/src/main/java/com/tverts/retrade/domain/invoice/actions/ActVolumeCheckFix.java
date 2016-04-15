package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.StoreGood;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Action builder to fix Volume Check Document
 * (implemented as an Invoice).
 *
 * @author anton.baukin@gmail.com
 */
public class ActVolumeCheckFix extends ActInvoiceBuySellFix
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);
		return Invoices.TYPE_VOLUME_CHECK.equals(tname);
	}


	/* protected: fix action methods */

	protected void initStoreGood (
	                 ActionBuildRec    abr,
	                 InvoiceStateFixed fstate,
	                 InvGood           igood,
	                 StoreGood         sgood
	               )
	{
		EX.assertx(
		  (igood.getVolume() != null) && CMP.greZero(igood.getVolume()),

		  "Volume Check Document [", target(abr, Invoice.class).getPrimaryKey(),
		  "] has good [", igood.getPrimaryKey(), "] having illegal left volume!"
		);

		//~: set good left volume
		sgood.setVolumeLeft(igood.getVolume().setScale(3));

		//HINT: volume+ and volume- components would be
		//  assigned by special aggregation calculator...
	}
}