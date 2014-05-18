package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;

/* com.tverts: actions */

import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: endure (core) */

import com.tverts.endure.Unity;

/* com.tverts: retrade views (document) */

import com.tverts.retrade.domain.doc.ActDocumentViewBase;
import com.tverts.retrade.domain.doc.DocumentView;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.Invoices;


/**
 * Creates actions to update DocumentView of
 * Buy-Sell Invoices.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   ActInvoiceBuySellUpdateDocumentView
       extends ActDocumentViewBase
{
	/* action types */

	public static final ActionType UPDATE =
	  ActionType.UPDATE;


	/* protected: ActDocumentViewBase interface */

	protected void    selectBuildActions(ActionBuildRec abr)
	{
		//?: {update request}
		if(UPDATE.equals(actionType(abr)))
			updateDocumentView(abr);
	}

	protected boolean isThatViewOwner(ActionBuildRec abr)
	{
		Unity u = viewOwner(abr).getUnity();
		return Invoices.isBuyInvoice(u) || Invoices.isSellInvoice(u);
	}


	/* protected: update action */

	protected void    updateDocumentView(ActionBuildRec abr)
	{
		//~: set transaction number
		chain(abr).first(new SetTxAction(task(abr)));

		//~: issue update action
		chain(abr).first(new UpdateInvoiceDocumentViewAction(
		  task(abr), viewOwner(abr, Invoice.class), documentView(abr)
		));

		complete(abr);
	}


	/* update document view action */

	public static class UpdateInvoiceDocumentViewAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public UpdateInvoiceDocumentViewAction
		  (ActionTask task, Invoice invoice, DocumentView view)
		{
			super(task);

			if(invoice == null) throw new IllegalArgumentException();
			this.invoice = invoice;

			if(view == null) throw new IllegalArgumentException();
			this.view    = view;
		}


		/* public: UpdateInvoiceDocumentViewAction interface */

		public Invoice      getInvoice()
		{
			return invoice;
		}

		public DocumentView getView()
		{
			return view;
		}

		/* public: Action interface */

		public DocumentView getResult()
		{
			return view;
		}


		/* protected: ActionBase interface */

		protected void     execute()
		  throws Throwable
		{
			//~: invoice state
			if(invoice.getInvoiceState() == null)
				view.setOwnerState(null);
			else
				view.setOwnerState(invoice.getInvoiceState().getStateType());

			//~: document type
			if(invoice.getInvoiceData().isAltered())
				view.setDocType(Invoices.getInvoiceAlteredType(invoice));
			else
				view.setDocType(null); //<-- the default type

			//~: document code
			view.setDocCode(invoice.getCode());

			//~: document date
			view.setDocDate(invoice.getInvoiceDate());

			//~: calculate the cost
			view.setDocCost(Invoices.getInvoiceGoodsCost(invoice));

			//~: trade store
			view.setStore(invoice.getInvoiceData().getStore());
		}


		/* private: references to the instances */

		private Invoice      invoice;
		private DocumentView view;
	}
}