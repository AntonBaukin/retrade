package com.tverts.retrade.domain.invoice.gen;

/* standard Java class */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;

/* com.tverts: retrade domain (firms + stores) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Generates Invoices of Move type.
 *
 * @author anton.baukin@gmail.com
 */
public class GenInvoiceMove extends GenInvoiceBase
{
	/* protected: GenInvoiceBase interface */

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		return new MoveData();
	}

	protected InvGood     createGood()
	{
		return new MoveGood();
	}

	protected void        assignGood(GenCtx ctx, InvGood good)
	{}

	protected void        assignStore(GenCtx ctx, InvoiceData data)
	{
		super.assignStore(ctx, data);

		//~: assign the source store
		assignSourceStore(ctx, data);
	}

	protected void        assignSourceStore(GenCtx ctx, InvoiceData data)
	{
		List<TradeStore> stores = new ArrayList<TradeStore>(
		  Arrays.asList(EX.assertn(
		    ctx.get(TradeStore[].class),
		    "Trade Stores were not generated in the Domain!"
		)));

		//~: remove the destination store
		stores.remove(data.getStore());

		//?: {not enough stores}
		EX.asserte(stores, "Not enough test Trade Stores were generated in the Domain!");

		//~: assign the source store
		((MoveData)data).setSourceStore(stores.get(
		  ctx.gen().nextInt(stores.size())));
	}

	protected Contractor  selectTestContractor(GenCtx ctx, Invoice invoice)
	{
		//HINT: move invoices has no contractors
		return null;
	}
}