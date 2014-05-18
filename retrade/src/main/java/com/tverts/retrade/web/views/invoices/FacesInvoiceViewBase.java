package com.tverts.retrade.web.views.invoices;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: faces */

import com.tverts.faces.UnityModelView;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: endure */

import static com.tverts.endure.ActionBuilderXRoot.SYNCH_AGGR;

/* com.tverts: model */

import com.tverts.model.ModelBean;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade (firms + invoices + payment) */

import com.tverts.retrade.domain.firm.Contractors;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceModelBean;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.payment.GetInvoiceBill;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * The base class of view of Invoice read-only
 * preview Faces back bean.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FacesInvoiceViewBase
       extends        UnityModelView
{
	/* public: FacesInvoiceView (bean) interface */

	public Invoice getEntity()
	{
		return (Invoice)super.getEntity();
	}


	/* public: view actions */

	public String  doFixInvoice()
	{
		actionRun(Invoices.ACT_FIX, getEntity(), SYNCH_AGGR, true);
		return "fixed";
	}

	public String  gotoEditInvoice()
	{
		return "edit";
	}

	public String  gotoEditFixedInvoice()
	{
		return "edit-fixed";
	}

	public String  gotoCancelEditInvoice()
	{
		return "cancel-edit";
	}


	/* public: view shared interface */

	public String  getInvoiceStateName()
	{
		return Invoices.getInvoiceStateName(getEntity());
	}

	public String  getInvoiceDateTime()
	{
		return DU.datetime2str(getEntity().getInvoiceDate());
	}

	public String  getInvoiceDate()
	{
		return DU.date2str(getEntity().getInvoiceDate());
	}

	public boolean isInvoiceEdited()
	{
		return Invoices.isInvoiceEdited(getEntity());
	}

	public boolean isInvoiceFixed()
	{
		return Invoices.isInvoiceFixed(getEntity());
	}

	public boolean isInvoiceAltered()
	{
		return getEntity().getInvoiceData().isAltered();
	}

	public String  getContractorName()
	{
		return Contractors.getContractorName(
		  bean(GetInvoiceBill.class).
		    getInvoiceBillContractor(getEntity()));
	}


	/* public: view [info] interface */

	public String  getWinmainTitleInfo()
	{
		return String.format("%s №%s от %s",
		  getEntity().getInvoiceType().getTitleLo(),
		  getEntity().getCode(),
		  DU.date2str(getEntity().getInvoiceDate())
		);
	}


	/* protected: UnityModelView interface */

	protected UnityModelBean createModelInstance()
	{
		return new InvoiceModelBean();
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof InvoiceModelBean);
	}
}