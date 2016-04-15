package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.StoreGood;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade domain (payments) */

import com.tverts.retrade.domain.payment.GetInvoiceBill;
import com.tverts.retrade.domain.payment.InvoiceBill;
import com.tverts.retrade.domain.payment.Payments;


/**
 * Action builder to change a {@link InvoiceStateFixed}
 * state of {@link Invoice}s with various types into
 * {@link InvoiceState} Edit state.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceEdit extends ActInvoiceChangeStateBase
{
	/* action types */

	public static final ActionType EDIT = Invoices.ACT_EDIT;


	/* protected: ActInvoiceBase (is that invoice) */

	/**
	 * This class works for invoices of all the types.
	 */
	protected boolean isThatInvoiceType(ActionBuildRec abr)
	{
		return true;
	}

	protected boolean isThatInvoiceState(ActionBuildRec abr)
	{
		return Invoices.typeInvoiceStateFixed().equals(getInvoiceStateType(abr));
	}


	/* protected: ActInvoiceBase (action build dispatching) */

	protected void selectBuildActions(ActionBuildRec abr)
	{
		//?: {edit request}
		if(EDIT.equals(actionType(abr)))
			editInvoice(abr);
	}


	/* protected: edit action methods */

	protected void editInvoice(ActionBuildRec abr)
	{
		//~: fire state change event
		reactStateChanged(abr);

		//~: defect the invoice bill
		defectInvoiceBill(abr);

		//~: update related entities
		updateInvoice(abr);

		//~: create aggregation requests
		xnest(abr, ActInvoiceBuySellAggr.AGGR_FIXED_TO_EDIT, target(abr));

		//~: set transaction number
		chain(abr).first(new SetTxAction(task(abr)));

		//~: assign the edit state
		assignEditState(abr);

		//~: delete the present (edit) state
		deleteFixedState(abr);

		complete(abr);
	}

	protected void assignEditState(ActionBuildRec abr)
	{
		//!: save + assign the state
		saveAssignState(abr, new InvoiceState());
	}

	protected void deleteFixedState(ActionBuildRec abr)
	{
		deleteState(abr);
	}

	protected void defectInvoiceBill(ActionBuildRec abr)
	{
		InvoiceBill ib = bean(GetInvoiceBill.class).
		  getInvoiceBill(target(abr, Invoice.class));

		if(ib != null)
			xnest(abr, Payments.INVOICE_BILL_DEFECT, ib);
	}


	/* delete state action */

	protected Action createDeleteStateAction(ActionBuildRec abr)
	{
		return new DeleteInvoiceBuySellFixedState(task(abr));
	}

	public static class DeleteInvoiceBuySellFixedState
	       extends      DeleteInvoiceState
	{
		/* public: constructor */

		public DeleteInvoiceBuySellFixedState(ActionTask task)
		{
			super(task);
		}


		/* protected: DeleteEntity (execution) */

		protected void doDelete()
		{
			//~: delete the goods instances
			InvoiceStateFixed state = (InvoiceStateFixed)getDeleteTarget();

			for(StoreGood g : state.getStoreGoods())
				session().delete(g);

			//~: delete the state itself
			super.doDelete();
		}
	}
}