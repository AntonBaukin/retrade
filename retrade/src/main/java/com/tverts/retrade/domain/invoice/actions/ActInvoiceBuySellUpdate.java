package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system (tx) */

import com.tverts.system.tx.SetTxAction;
import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionTask;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionWithTxBase;

/* com.tverts: retrade domain (views + goods) */

import com.tverts.retrade.domain.doc.DocumentView;
import com.tverts.retrade.domain.doc.GetDocumentView;

/* com.tverts: retrade (firm + invoice + payment) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.payment.GetInvoiceBill;
import com.tverts.retrade.domain.payment.InvoiceBill;


/**
 * Action builder to update Buy or Sell {@link Invoice}s
 * being in {@link InvoiceState} Edit state.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class ActInvoiceBuySellUpdate extends ActInvoiceBuySellSave
{
	/* action types */

	public static final ActionType UPDATE = Invoices.ACT_UPDATE;


	/* protected: ActInvoiceBase (is that invoice) */

	/**
	 * Update operation supports Invoices of various states.
	 */
	protected boolean isThatInvoiceState(ActionBuildRec abr)
	{
		return true;
	}


	/* protected: ActInvoiceBase (action build dispatching) */

	protected void selectBuildActions(ActionBuildRec abr)
	{
		buildUpdateAction(abr);
	}

	protected void buildUpdateAction(ActionBuildRec abr)
	{
		//?: {update request}
		if(UPDATE.equals(actionType(abr)))
			updateInvoice(abr);
	}


	/* protected: update action methods */

	protected void updateInvoice(ActionBuildRec abr)
	{
		//~: check the target class
		checkTargetClass(abr, Invoice.class);

		//~: recalculate the invoice bill
		updateInvoiceBill(abr);

		//~: update the views  !: after the invoice
		updateInvoiceViews(abr);

		//~: update resulting goods
		prepareInvoiceData(abr);

		//~: set transaction number
		chain(abr).first(new SetTxAction(task(abr)));

		//~: process the edit instance
		if(Invoices.isInvoiceEdited(target(abr, Invoice.class)))
			updateFromInvoiceEdit(abr);

		abr.setComplete();
	}

	protected void updateInvoiceViews(ActionBuildRec abr)
	{
		//~: update the document view of this invoice
		updateInvoiceDocumentView(abr);
	}

	protected void updateInvoiceDocumentView(ActionBuildRec abr)
	{
		DocumentView v = bean(GetDocumentView.class).
		  findDocumentView(target(abr, Invoice.class).getPrimaryKey());

		//~: update the document view of this invoice
		if(v != null) xnest(abr, ActionType.UPDATE, v);
	}

	protected void updateInvoiceBill(ActionBuildRec abr)
	{
		Long contractor = null;

		//?: {invoice is edited} update the contractor
		if(Invoices.isInvoiceEdited(target(abr, Invoice.class)))
			if(param(abr, INVOICE_EDIT) != null)
				contractor = param(abr, INVOICE_EDIT, InvoiceEdit.class).getContractor();

		//~: update the bill action
		chain(abr).first(new UpdateInvoiceBillAction(task(abr)).
		  setNewContractor(contractor));
	}

	protected void updateFromInvoiceEdit(ActionBuildRec abr)
	{
		//?: {has no invoice edit} quit
		if(param(abr, INVOICE_EDIT) == null) return;

		//~: update the invoice order
		orderInvoiceUpdated(abr);

		//~: insert assign action
		assignInvoiceEdit(abr);
	}

	protected void assignInvoiceEdit(ActionBuildRec abr)
	{
		chain(abr).first(new AssignInvoiceEditAction(
		  task(abr), param(abr, INVOICE_EDIT, InvoiceEdit.class)));
	}

	protected void orderInvoiceUpdated(ActionBuildRec abr)
	{
		Invoice     i = target(abr, Invoice.class);
		InvoiceEdit e = param(abr, INVOICE_EDIT, InvoiceEdit.class);
		Action      a = e.createOrderAction(abr, i);

		//?: invoice must be reordered
		if(a != null)
			chain(abr).first(a);
	}


	/* assign invoice edit action */

	public static class AssignInvoiceEditAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public AssignInvoiceEditAction(ActionTask task, InvoiceEdit ie)
		{
			super(task);

			if((ie == null) || (ie.getObjectKey() == null))
				throw new IllegalArgumentException(
				  "The Invoice Edit is not defined!");

			if(!ie.objectKey().equals(target(Invoice.class).getPrimaryKey()))
				throw new IllegalArgumentException(
				  "The Invoice Edit is not for the Invoice instance is being edited!");

			this.invoiceEdit = ie;
		}


		/* public: AssignInvoiceEditAction interface */

		public InvoiceEdit getInvoiceEdit()
		{
			return invoiceEdit;
		}


		/* public: Action interface */

		public Invoice     getResult()
		{
			return target(Invoice.class);
		}


		/* protected: ActionBase interface */

		protected void     execute()
		  throws Throwable
		{
			Invoice     i = target(Invoice.class);
			InvoiceEdit e = getInvoiceEdit();

			//~: remove obsolete goods
			deleteObsoleteGoods();

			//~: insert the new goods
			e.insertNewGoods(i);

			//~: assign the properties of the edited goods
			e.assignGoods(i);

			//~: assign the properties of the invoice
			e.assignProps(i);
		}

		protected void     deleteObsoleteGoods()
		{
			Invoice     i = target(Invoice.class);
			InvoiceEdit e = getInvoiceEdit();

			for(InvGood g : e.removeObsoleteGoods(i))
				deleteInvoiceGood(g);
		}

		protected void     deleteInvoiceGood(InvGood g)
		{
			//!: delete invoice good directly
			session().delete(g);
		}


		/* protected: the invoice edit */

		private InvoiceEdit invoiceEdit;
	}


	public static class UpdateInvoiceBillAction
	       extends      ActionWithTxBase
	{
		/* public: constructor */

		public UpdateInvoiceBillAction(ActionTask task)
		{
			super(task);
		}


		/* public: Action interface */

		public InvoiceBill getResult()
		{
			return getInvoiceBill();
		}


		/* public: UpdateInvoiceBillAction interface */

		public UpdateInvoiceBillAction setNewContractor(Long co)
		{
			this.newContractor = co;
			return this;
		}


		/* protected: ActionBase interface */

		protected void     execute()
		  throws Throwable
		{
			Invoice i = target(Invoice.class);
			boolean b = Invoices.isBuyInvoice(i);
			boolean s = Invoices.isSellInvoice(i);

			if(getInvoiceBill() == null)
			{
				if(b || s) throw new IllegalStateException(String.format(
				  "Buy or Sell Invoice pkey [%d] has no Invoice Bill!",
				  i.getPrimaryKey()
				));

				return;
			}

			//~: assign new contractor
			if(newContractor != null)
				updateInvoiceBillContractor(newContractor);
			//?: {Buy | Sell Invoice has no Contra}
			else if((b || s) && (getInvoiceBill().getContractor() == null))
				throw new IllegalStateException(String.format(
				  "Buy or Sell Invoice pkey [%d] has no Contractor assigned!",
				  i.getPrimaryKey()
				));

			//~: update Txn
			TxPoint.txn(tx(), getInvoiceBill());
		}


		/* protected: InvoiceBill access */

		protected InvoiceBill getInvoiceBill()
		{
			if(hasInvoiceBill != null)
				return invoiceBill;

			invoiceBill    = findInvoiceBill();
			hasInvoiceBill = (invoiceBill != null);

			return invoiceBill;
		}

		protected InvoiceBill findInvoiceBill()
		{
			return bean(GetInvoiceBill.class).
			  getInvoiceBill(target(Invoice.class));
		}


		/* protected: update */

		protected void        updateInvoiceBillContractor(Long co)
		{
			Contractor obj = bean(GetContractor.class).getContractor(co);
			if(obj == null) throw new IllegalStateException();

			getInvoiceBill().setContractor(obj);
		}


		/* private: invoice bill */

		private InvoiceBill invoiceBill;
		private Long        newContractor;
		private Boolean     hasInvoiceBill;
	}
}