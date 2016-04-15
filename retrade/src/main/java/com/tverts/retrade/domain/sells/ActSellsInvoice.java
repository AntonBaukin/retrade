package com.tverts.retrade.domain.sells;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;
import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.ResGood;
import com.tverts.retrade.domain.invoice.SellGood;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceBuySellAggr;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceSaveBase;

/* com.tverts: retrade domain (stores) */

import com.tverts.retrade.domain.store.StoreGood;


/**
 * Action Builder to process Sells Invoices.
 *
 * Sells Invoices differ from Sell Invoices.
 * They are produced by Sells Sessions, and have
 * no Contractor related (and the Bills) as sells
 * are made via POS terminals.
 *
 * As Sells Session may withdraw goods from several
 * Stores, it in general produces more that one
 * Sells Invoice: each for the distinct Store.
 *
 * Sells Invoices are saved in the Fixed State!
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActSellsInvoice extends ActInvoiceSaveBase
{
	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		return Sells.TYPE_INVOICE.equals(getInvoiceTypeName(abr));
	}

	protected boolean isThatInvoiceState(ActionBuildRec abr)
	{
		return Invoices.typeInvoiceStateFixed().equals(getInvoiceStateType(abr));
	}


	/* protected: ActInvoiceBase (save action methods) */

	protected void assignInvoicePrimaryKeys(ActionBuildRec abr)
	{
		Invoice           i = target(abr, Invoice.class);
		SellsData         d = (SellsData) i.getInvoiceData();
		InvoiceStateFixed s = (InvoiceStateFixed) i.getInvoiceState();

		super.assignInvoicePrimaryKeys(abr);

		//~: assign the keys of the sell goods
		for(SellGood g : d.getGoods())
			setPrimaryKey(session(abr), g, isTestInstance(i));

		//~: assign the keys of the result goods
		for(ResGood g : d.getResGoods())
			setPrimaryKey(session(abr), g, isTestInstance(i));

		//~: assign the keys of the store goods
		for(StoreGood g : s.getStoreGoods())
			setPrimaryKey(session(abr), g, isTestInstance(i));
	}

	protected void aggrInvoice(ActionBuildRec abr)
	{
		//~: create aggregation requests
		xnest(abr, ActInvoiceBuySellAggr.AGGR_EDIT_TO_FIXED, target(abr),
		  ActInvoiceBuySellAggr.INVOICE_STATE_TYPE, Invoices.typeInvoiceStateFixed()
		);
	}
}