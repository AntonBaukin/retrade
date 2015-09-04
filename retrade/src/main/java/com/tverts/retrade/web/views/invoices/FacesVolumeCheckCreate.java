package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;

/* com.tverts: endure */

import com.tverts.endure.UnityType;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade (invoices + stores) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;


/**
 * Back bean to create model of Volume Check Invoices.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class   FacesVolumeCheckCreate
       extends FacesVolumeCheckEdit
{
	/* public: view actions */

	public String    doSubmitEditInvoice()
	{
		//~: assign the goods from the request
		if(!assignRequestedGoods()) return null;

		//~: create new invoice
		Invoice invoice = getModel().
		  getInvoice().createInvoice();

		//~: temporary set max order index
		invoice.setOrderIndex(Long.MAX_VALUE);

		//!: issue save+edit action
		ActionsPoint.actionRun(Invoices.ACT_SAVE, invoice,
		  ActionsPoint.SYNCH_AGGR, true,
		  Invoices.INVOICE_TYPE, getInvoiceType(),
		  Invoices.INVOICE_STATE_TYPE, Invoices.typeInvoiceStateEdited(),
		  Invoices.INVOICE_EDIT, getModel().getInvoice()
		);

		return null;
	}

	/* public: [edit] interface */

	public UnityType getInvoiceType()
	{
		return Invoices.typeVolumeCheck();
	}

	public String    getWinmainTitleEdit()
	{
		return "Создать инвентаризацию остатков";
	}

	public void      forceSecureEdit()
	{
		forceSecure("create: volume check");
	}


	/* protected: ModelView interface */

	protected ModelBean createModel()
	{
		//~: create model & empty edit model
		InvoiceEditModelBean m = new InvoiceEditModelBean();
		InvoiceEdit          e = new InvoiceEdit();

		m.setInvoice(e);

		//~: order type (main)
		e.setOrderType(Invoices.typeInvoiceBuySellOrder());

		//~: domain
		m.setDomain(getDomainKey());
		e.setDomain(getDomainKey());

		//~: invoice type
		e.setInvoiceType(getInvoiceType().getPrimaryKey());
		e.setInvoiceTypeName(getInvoiceType().getTitleLo());

		//~: invoice state name (edited)
		e.setInvoiceStateName(Invoices.typeInvoiceStateEdited().getTitleLo());

		//~: date (present)
		e.setInvoiceDate(new java.util.Date());
		e.setEditDate(e.getInvoiceDate());

		//~: code (new, incremented)
		e.setInvoiceCode(Invoices.
		  createInvoiceCode(getDomain(), getInvoiceType()));

		//~: trade (destination) store (default)
		TradeStore store = bean(GetTradeStore.class).
		  getStoreByOffset(getDomainKey(), 0);

		if(store != null)
			e.setTradeStore(store.getPrimaryKey());

		return m;
	}
}