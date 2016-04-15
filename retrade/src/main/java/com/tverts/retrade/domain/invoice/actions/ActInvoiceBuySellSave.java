package com.tverts.retrade.domain.invoice.actions;


/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;
import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.SellData;

/* com.tverts: retrade (firm + payment) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.payment.InvoiceBill;


/**
 * Action builder to save Buy or Sell {@link Invoice}s in
 * {@link InvoiceState} Edit state.
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceBuySellSave extends ActInvoiceSaveBase
{
	/* action builder parameters */

	public static final String CONTRACTOR =
	  Invoices.INVOICE_CONTRACTOR;


	/* protected: ActInvoiceBase (is that invoice) */

	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		String tname = getInvoiceTypeName(abr);

		return Invoices.TYPE_INVOICE_BUY.equals(tname) ||
		  Invoices.TYPE_INVOICE_SELL.equals(tname);
	}

	protected boolean isThatInvoiceState(ActionBuildRec abr)
	{
		return Invoices.typeInvoiceStateEdited().equals(getInvoiceStateType(abr));
	}


	/* protected: ActInvoiceBase (save action methods) */

	protected void saveInvoice(ActionBuildRec abr)
	{
		//~: save the Bill of Buy-Sell Invoice
		saveInvoiceBill(abr);

		super.saveInvoice(abr);
	}

	protected void assignInvoicePrimaryKeys(ActionBuildRec abr)
	{
		super.assignInvoicePrimaryKeys(abr);

		//~: assign the keys of the goods
		Invoice i = target(abr, Invoice.class);
		for(InvGood g : i.getInvoiceData().getGoods())
			if(g.getPrimaryKey() == null)
				setPrimaryKey(session(abr), g, isTestInstance(i));
	}

	protected void prepareInvoiceData(ActionBuildRec abr)
	{
		//?: {update data of a sell invoice}
		if(getInvoiceData(abr) instanceof SellData)
			chain(abr).first(new UpdateInvoiceResults(task(abr)));
	}

	protected void saveInvoiceBill(ActionBuildRec abr)
	{
		//?: {is Contractor type and is defined}
		if(param(abr, CONTRACTOR, Contractor.class) == null)
			return;

		InvoiceBill ib = new InvoiceBill();

		//~: init the Bill instance state
		initInvoiceBill(abr, ib);

		//!: do save it
		xnest(abr, ActionType.SAVE, ib);
	}

	protected void initInvoiceBill(ActionBuildRec abr, InvoiceBill ib)
	{
		Invoice i  = target(abr, Invoice.class);

		//~: primary key
		setPrimaryKey(session(abr), ib, isTestInstance(i));

		//~: contractor
		ib.setContractor(param(abr, CONTRACTOR, Contractor.class));

		//~: invoice
		ib.setInvoice(i);

		//HINT: as invoice is being saved in edit state always,
		//  expense & income of bill are undefined...
	}
}