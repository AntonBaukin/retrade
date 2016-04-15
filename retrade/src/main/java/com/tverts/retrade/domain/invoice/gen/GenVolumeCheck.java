package com.tverts.retrade.domain.invoice.gen;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.VolData;
import com.tverts.retrade.domain.invoice.VolGood;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Generator for Volume Check Documents (Invoices).
 *
 * @author anton.baukin@gmail.com
 */
public class GenVolumeCheck extends GenInvoiceBase
{
	/* protected: GenInvoiceBase interface */

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		return new VolData();
	}

	protected InvGood     createGood()
	{
		return new VolGood();
	}

	protected void        assignGood(GenCtx ctx, InvGood good)
	{}

	protected Contractor  selectTestContractor(GenCtx ctx, Invoice invoice)
	{
		//HINT: volume checks has no contractors
		return null;
	}
}