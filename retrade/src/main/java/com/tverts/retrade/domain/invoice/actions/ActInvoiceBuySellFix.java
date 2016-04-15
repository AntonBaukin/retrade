package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: system (tx) */

import com.tverts.retrade.domain.invoice.ResGood;
import com.tverts.retrade.domain.invoice.SellData;
import com.tverts.support.EX;
import com.tverts.system.tx.SetTxAction;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.StoreGood;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.SellGood;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.GetInvoiceBill;
import com.tverts.retrade.domain.payment.InvoiceBill;
import com.tverts.retrade.domain.payment.Payments;


/**
 * Action builder to change a {@link InvoiceState} Edit
 * state of Buy or Sell {@link Invoice}s into
 * {@link InvoiceStateFixed} state.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceBuySellFix extends ActInvoiceChangeStateBase
{
	/* action types */

	public static final ActionType FIX = Invoices.ACT_FIX;


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


	/* protected: ActInvoiceBase (action build dispatching) */

	protected void selectBuildActions(ActionBuildRec abr)
	{
		//?: {fix request}
		if(FIX.equals(actionType(abr)))
			fixInvoice(abr);
	}


	/* protected: fix action methods */

	protected void fixInvoice(ActionBuildRec abr)
	{
		//~: fire state change event
		reactStateChanged(abr);

		//~: effect the invoice bill
		effectInvoiceBill(abr);

		//~: update related entities
		updateInvoice(abr);

		//~: create aggregation requests
		xnest(abr, ActInvoiceBuySellAggr.AGGR_EDIT_TO_FIXED, target(abr));

		//~: set transaction number
		chain(abr).first(new SetTxAction(task(abr)));

		//~: assign the fixed state
		assignFixedState(abr);

		//~: delete the present (edit) state
		deleteEditState(abr);

		complete(abr);
	}

	protected void assignFixedState(ActionBuildRec abr)
	{
		InvoiceStateFixed fstate = new InvoiceStateFixed();

		//~: copy the goods
		copyStateGoods(abr, fstate);

		//!: save + assign the state
		saveAssignState(abr, fstate);
	}

	protected void deleteEditState(ActionBuildRec abr)
	{
		deleteState(abr);
	}

	protected void effectInvoiceBill(ActionBuildRec abr)
	{
		InvoiceBill ib = bean(GetInvoiceBill.class).
		  getInvoiceBill(target(abr, Invoice.class));

		if(ib != null)
			xnest(abr, Payments.INVOICE_BILL_EFFECT, ib);
	}


	/* protected: goods copying */

	/**
	 * Copies the goods from the given edit state
	 * (or Invoice Data) to the fixed state.
	 */
	protected void    copyStateGoods(ActionBuildRec abr, InvoiceStateFixed fstate)
	{
		//?: {the Invoice is not altered}
		if(!target(abr, Invoice.class).getInvoiceData().isAltered())
			copyDataGoods(abr, fstate);
		//?: {alternated invoices of this type are not supported}
		else if(!isAltInvoiceSupported(abr))
			throw EX.state("Fixing altered Invoice [",
			  target(abr, Invoice.class).getPrimaryKey(), "] is not supported!");
		//~: copy resulting goods
		else
			copyResultGoods(abr, fstate);

	}

	protected void    copyDataGoods(ActionBuildRec abr, InvoiceStateFixed fstate)
	{
		InvoiceData data = target(abr, Invoice.class).getInvoiceData();

		//~: copy the goods
		for(InvGood igood : data.getGoods())
			insertStoreGood(abr, fstate, igood);
	}

	protected boolean isAltInvoiceSupported(ActionBuildRec abr)
	{
		return (target(abr, Invoice.class).getInvoiceData() instanceof SellData);
	}

	protected void    copyResultGoods(ActionBuildRec abr, InvoiceStateFixed fstate)
	{
		InvoiceData data = target(abr, Invoice.class).getInvoiceData();

		//~: copy the resulting goods
		for(InvGood igood : data.getResGoods())
			insertStoreGood(abr, fstate, igood);
	}

	protected void    insertStoreGood (
	    ActionBuildRec    abr,
	    InvoiceStateFixed fstate,
	    InvGood           igood
	  )
	{
		TradeStore store = target(abr, Invoice.class).
		  getInvoiceData().getStore();

		StoreGood  sgood = new StoreGood();

		//~: set the primary key
		setPrimaryKey(session(abr), sgood, isTestTarget(abr));

		//~: store
		sgood.setStore(store);

		//~: invoice' state
		sgood.setInvoiceState(fstate);
		fstate.getStoreGoods().add(sgood);

		//~: good unit
		sgood.setGoodUnit(igood.getGoodUnit());

		//~: set good volume and the cost
		initStoreGood(abr, fstate, igood, sgood);
	}

	protected void    initStoreGood (
	    ActionBuildRec    abr,
	    InvoiceStateFixed fstate,
	    InvGood           igood,
	    StoreGood         sgood
	  )
	{
		//~: +volume
		if(igood instanceof BuyGood)
			sgood.setVolumePositive(igood.getVolume());

		//~: -volume
		if(igood instanceof SellGood)
			sgood.setVolumeNegative(igood.getVolume());

		//~: +/- volume
		if(igood instanceof ResGood)
		{
			//~: -volume
			sgood.setVolumeNegative(igood.getVolume());

			//?: {production transient good}
			if(((ResGood)igood).getGoodCalc() != null)
				sgood.setVolumePositive(igood.getVolume());
		}
	}
}