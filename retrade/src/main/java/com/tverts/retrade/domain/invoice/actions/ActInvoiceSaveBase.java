package com.tverts.retrade.domain.invoice.actions;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;
import static com.tverts.hibery.HiberPoint.isTestInstance;

/* com.tverts: actions */

import com.tverts.actions.Action;
import com.tverts.actions.ActionBuildRec;
import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsCollection.SaveNumericIdentified;
import com.tverts.actions.ActionsCollection.SaveViewBase;

/* com.tverts: endure core */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.ActUnity;

/* com.tverts: retrade domain (views) */

import com.tverts.retrade.domain.doc.DocumentView;
import com.tverts.retrade.domain.invoice.actions.ActInvoiceBuySellUpdateDocumentView.
  UpdateInvoiceDocumentViewAction;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceState;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Basic implementation for action builders
 * saving the {@link Invoice}s.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ActInvoiceSaveBase
       extends        ActInvoiceBase
{
	/* action types */

	public static final ActionType SAVE = Invoices.ACT_SAVE;


	/* action builder parameters */

	public static final String INVOICE_EDIT      =
	  Invoices.INVOICE_EDIT;

	public static final String ORDER_NOT         =
	  Invoices.ORDER_NOT;

	public static final String ORDER_TYPE        =
	  Invoices.ORDER_TYPE;

	public static final String ORDER_REFERENCE   =
	  Invoices.ORDER_REFERENCE;

	public static final String ORDER_BEFOREAFTER =
	  Invoices.ORDER_BEFOREAFTER;


	/* protected: ActInvoiceBase (action build dispatching) */

	protected void selectBuildActions(ActionBuildRec abr)
	{
		buildSaveAction(abr);
	}

	protected void buildSaveAction(ActionBuildRec abr)
	{
		//?: {save request}
		if(SAVE.equals(actionType(abr)))
			saveInvoice(abr);
	}


	/* protected: save action methods */

	protected void saveInvoice(ActionBuildRec abr)
	{
		//~: created event
		reactCreated(abr);

		//~: create & update views
		saveInvoiceViews(abr);

		//~: create special aggregations
		aggrInvoice(abr);

		//~: order the invoice
		orderInvoiceSaved(abr);

		//HINT: invoice state refers the invoice,
		//  and it must be saved later.

		//~: save invoice state
		saveInvoiceState(abr);

		//~: save the invoice
		createInvoiceSaveAction(abr);

		//~: prepare invoice data
		prepareInvoiceData(abr);

		//~: set invoice' unity       <-- is executed first
		createInvoiceUnity(abr);

		//~: assign primary keys
		assignInvoicePrimaryKeys(abr);

		complete(abr);
	}

	protected void assignInvoicePrimaryKeys(ActionBuildRec abr)
	{
		Invoice      i = target(abr, Invoice.class);
		InvoiceState s = i.getInvoiceState();

		if(i.getPrimaryKey() == null)
			setPrimaryKey(session(abr), i,
			  isTestInstance(i.getDomain())
			);

		if((s != null) && (s.getPrimaryKey() == null))
			setPrimaryKey(session(abr), s, isTestInstance(i));
	}

	protected void createInvoiceSaveAction(ActionBuildRec abr)
	{
		chain(abr).first(new SaveNumericIdentified(task(abr)));
	}

	protected void createInvoiceUnity(ActionBuildRec abr)
	{
		xnest(abr, ActUnity.CREATE, target(abr),
		  ActUnity.UNITY_TYPE, getInvoiceType(abr));
	}

	protected void prepareInvoiceData(ActionBuildRec abr)
	{
		//HINT: Invoice Data is cascaded.
	}

	protected void saveInvoiceState(ActionBuildRec abr)
	{
		//~: save the state
		createInvoiceStateSaveAction(abr);

		//~: set state' unity         <-- is executed first
		createInvoiceStateUnity(abr);
	}

	protected void createInvoiceStateSaveAction(ActionBuildRec abr)
	{
		chain(abr).first(new SaveNumericIdentified(
		  task(abr), getInvoiceState(abr)));
	}

	protected void createInvoiceStateUnity(ActionBuildRec abr)
	{
		xnest(abr, ActUnity.CREATE, getInvoiceState(abr),
		  ActUnity.UNITY_TYPE, getInvoiceStateType(abr));
	}

	protected void orderInvoiceSaved(ActionBuildRec abr)
	{
		if(flag(abr, ORDER_NOT))
			return;

		Invoice     i  = target(abr, Invoice.class);
		InvoiceEdit ie = null;
		Action      a  = null;

		//?: {has no order type}
		if(param(abr, ORDER_TYPE) != null)
		{
			Object ot = param(abr, ORDER_TYPE);

			if(ot instanceof String)
				ot = SU.sXe((String)ot)?(null):
				  (UnityTypes.unityType(Invoice.class, (String)ot));

			if((ot != null) && !(ot instanceof UnityType))
				throw new IllegalArgumentException();

			i.setOrderType((UnityType)ot);
		}

		//?: {has edit instance}
		if(param(abr, INVOICE_EDIT) != null)
			ie = param(abr, INVOICE_EDIT, InvoiceEdit.class);
		//?: {has date} create temporary edit instance
		else if((i.getInvoiceDate() != null) && (i.getOrderType() != null))
		{
			ie = new InvoiceEdit().init(i);

			//HINT: require order as the dates are equal
			i.setOrderIndex(Long.MAX_VALUE);
		}

		//?: {has edit state} create the action
		if(ie != null)
			a = ie.createOrderAction(abr, i);

		//?: {has create-edit specific action}
		if(a != null)
			chain(abr).first(a);
	}

	protected void aggrInvoice(ActionBuildRec abr)
	{}

	protected void saveInvoiceViews(ActionBuildRec abr)
	{
		//~: save the document view
		saveInvoiceDocumentView(abr, new DocumentView());
	}

	protected void saveInvoiceDocumentView(ActionBuildRec abr, DocumentView v)
	{
		//~: do save it
		chain(abr).first(new SaveViewBase(task(abr),
		  v, target(abr, Invoice.class)));

		//~: do update it
		chain(abr).first(new UpdateInvoiceDocumentViewAction(
		  task(abr), target(abr, Invoice.class), v
		));
	}
}