package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelViewBase;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceModelBean;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade (support) */

import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Special controller for shared implementation
 * of (fixed) Invoice ask-edit page.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesInvoiceAskEdit extends ModelViewBase
{
	/* actions */

	public String  gotoCancelEdit()
	{
		return "cancel-edit-" + getGotoCode();
	}

	public String  doEditInvoice()
	{
		actionRun(Invoices.ACT_EDIT, getViewInvoice());
		return "edit-" + getGotoCode();
	}


	/* public: invoice [view] interface */

	public String  getGotoCode()
	{
		return Invoices.getInvoiceGotoCode(getViewInvoice().getInvoiceType());
	}

	public Invoice getViewInvoice()
	{
		return EX.assertn(
		  findRequestedModelAssignable(InvoiceModelBean.class),
		  "Can't obtain Invoice View model from the request!"
		).
		  invoice();
	}

	public Long    getInvoiceKey()
	{
		return getViewInvoice().getPrimaryKey();
	}

	public String  getWindowTitleAskEdit()
	{
		Invoice i = getViewInvoice();

		return formatTitle("Редактировать накладную?",
		  i.getCode(), DU.datetime2str(i.getInvoiceDate())
		);
	}
}