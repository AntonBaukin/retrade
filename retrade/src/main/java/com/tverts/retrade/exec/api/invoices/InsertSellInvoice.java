package com.tverts.retrade.exec.api.invoices;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.SellData;
import com.tverts.retrade.domain.invoice.SellGood;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.retrade.goods.GoodSell;
import com.tverts.api.retrade.document.BuySell;
import com.tverts.api.retrade.document.Sell;


/**
 * Inserts new Sell Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertSellInvoice extends InsertInvoiceBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof Sell);
	}


	/* protected: UpdateInvoiceBase interface */

	protected String      getInvoiceTypeName()
	{
		return Invoices.TYPE_INVOICE_SELL;
	}

	protected InvoiceData createInvoiceData()
	{
		return new SellData();
	}

	protected InvGood     createGood()
	{
		return new SellGood();
	}


	/* protected: state and data creation */

	protected void copyGood(BuySell bs, SellGood ig, GoodSell gs)
	{
		super.copyGood(bs, ig, gs);

		//~: good cost
		ig.setCost(copyGoodCost(bs.getXkey(), gs));

		//~: assign the price list
		assignGoodPrice(bs.getXkey(), ig, gs);
	}
}