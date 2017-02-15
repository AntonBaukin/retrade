package com.tverts.retrade.web.views.invoices;

/* Java */

import java.math.BigDecimal;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: endure */

import com.tverts.endure.UnityType;

/* com.tverts: retrade (trade stores + invoices) */

import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Back bean for edit model of Move Invoices
 * modified to create instances.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceMoveCreate
       extends FacesInvoiceMoveEdit
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
		actionRun(Invoices.ACT_SAVE, invoice,
		  Invoices.INVOICE_TYPE, getInvoiceType(),
		  Invoices.INVOICE_STATE_TYPE, Invoices.typeInvoiceStateEdited(),
		  Invoices.INVOICE_EDIT, getModel().getInvoice()
		);

		return null;
	}


	/* public: view [edit] interface */

	public UnityType getInvoiceType()
	{
		return Invoices.typeInvoiceMove();
	}

	public String    getWindowTitleEdit()
	{
		if(isAutoProduce())
			return "Создание накладной авто-производства";
		if(isFreeProduce())
			return "Создание накладной производства";
		if(isCorrection())
			return "Создание накладной корректировки объёмов";
		return "Создание накладной перемещения";
	}

	public void      forceSecureEdit()
	{
		forceSecure("create: invoice: move");
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

		//~: invoice sub-type provided
		String    tp = SU.s2s(request().getParameter("invoice-type"));
		UnityType it = getInvoiceType();

		if("auto".equals(tp))
		{
			it = Invoices.typeInvoiceAutoProduce();
			e.setSubType('A');
		}
		else if("free".equals(tp))
		{
			it = Invoices.typeInvoiceFreeProduce();
			e.setSubType('P');
		}
		else if("correct".equals(tp))
		{
			it = Invoices.typeInvoiceCorrection();
			e.setSubType('C');
		}

		//~: invoice type
		e.setInvoiceType(getInvoiceType().getPrimaryKey());
		e.setInvoiceTypeName(getInvoiceType().getTitleLo());

		//~: invoice state name (edited)
		e.setInvoiceStateName(Invoices.typeInvoiceStateEdited().getTitleLo());

		//~: date (present)
		e.setInvoiceDate(new java.util.Date());
		e.setEditDate(e.getInvoiceDate());

		//~: goods cost (zero)
		e.setGoodsCost(BigDecimal.ZERO);

		//~: code (new, incremented)
		e.setInvoiceCode(Invoices.createInvoiceCode(getDomain(), it));

		//~: trade (destination) store (default)
		TradeStore store = bean(GetTradeStore.class).
		  getStoreByOffset(getDomainKey(), 0);

		if(store != null)
			e.setTradeStore(store.getPrimaryKey());

		//~: trade (source) store (default)
		store = bean(GetTradeStore.class).
		  getStoreByOffset(getDomainKey(), 1);

		if(store != null)
			e.setTradeStoreSource(store.getPrimaryKey());

		return m;
	}
}