package com.tverts.retrade.web.views.invoices;

/* standard Java classes */

import java.math.BigDecimal;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: endure */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import static com.tverts.endure.ActionBuilderXRoot.SYNCH_AGGR;

/* com.tverts: retrade (firm + store + invoice) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Back bean for edit model of Sell Invoices
 * modified to create instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesInvoiceSellCreate
       extends FacesInvoiceSellEdit
{
	/* public: view actions */

	public String  doSubmitEditInvoice()
	{
		//~: assign the goods from the request
		if(!assignRequestedGoods()) return null;

		//~: create new invoice
		Invoice invoice = getModel().
		  getInvoice().createInvoice();

		//~: temporary set max order index
		invoice.setOrderIndex(Long.MAX_VALUE);

		//~: contractor
		Contractor co = EX.assertn(bean(GetContractor.class).
		  getContractor(getModel().getInvoice().getContractor()));

		//!: issue save+edit action
		actionRun(Invoices.ACT_SAVE, invoice, SYNCH_AGGR, true,
		  Invoices.INVOICE_TYPE, getInvoiceType(),
		  Invoices.INVOICE_STATE_TYPE, Invoices.typeInvoiceStateEdited(),
		  Invoices.INVOICE_EDIT, getModel().getInvoice(),
		  Invoices.INVOICE_CONTRACTOR, co
		);

		return null;
	}


	/* public: view [edit] interface */

	public UnityType getInvoiceType()
	{
		return Invoices.typeInvoiceSell();
	}

	public String    getWinmainTitleEdit()
	{
		return "Создание накладной продажи";
	}

	public void      forceSecureEdit()
	{
		forceSecure("create: invoice: sell");
	}


	/* protected: ModelView interface */

	protected ModelBean createModel()
	{
		//~: create model & empty edit model
		InvoiceEditModelBean m = new InvoiceEditModelBean();
		InvoiceEdit          e = new InvoiceEdit();

		m.setInvoice(e);

		//~: order type
		e.setOrderType(UnityTypes.unityType(
		  Invoice.class, Invoices.OTYPE_INV_BUYSELL
		));

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

		//~: goods cost (zero)
		e.setGoodsCost(BigDecimal.ZERO);

		//~: code (new, incremented)
		e.setInvoiceCode(Invoices.createInvoiceCode(
		  getDomain(), getInvoiceType()
		));

		//~: trade store (default)
		TradeStore store = bean(GetTradeStore.class).
		  getStoreByOffset(getDomainKey(), 0);

		if(store != null)
			e.setTradeStore(store.getPrimaryKey());

		return m;
	}
}