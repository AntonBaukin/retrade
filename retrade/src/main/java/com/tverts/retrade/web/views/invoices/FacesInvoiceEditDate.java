package com.tverts.retrade.web.views.invoices;

/* standard Java classes */

import java.util.Date;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade (support) */

import com.tverts.support.DU;


/**
 * Special controller for shared implementation
 * of (edited) Invoice edit date page.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceEditDate
       extends FacesInvoiceSpecialEditBase
{
	/* actions */

	public String  gotoCancelEdit()
	{
		getInvoice().setEditDate(null);
		return "done-edit-" + getGotoCode();
	}

	public String  doSaveInvoiceDate()
	{
		getInvoice().setInvoiceDate(getInvoice().getEditDate());
		return "done-edit-" + getGotoCode();
	}


	/* public: invoice [edit date] interface */

	public String  getWindowTitleEditDate()
	{
		return formatTitle("Ред. даты накладной",
		  getInvoice().getInvoiceCode()
		);
	}

	public Date    getInvoiceDatePart()
	{
		return getInvoice().getEditDate();
	}

	public void    setInvoiceDatePart(Date invoiceDatePart)
	{
		getInvoice().setEditDate(DU.merge(
		  invoiceDatePart, getInvoiceTimePart()
		));
	}

	public Date    getInvoiceTimePart()
	{
		return getInvoice().getEditDate();
	}

	public void    setInvoiceTimePart(Date invoiceTimePart)
	{
		getInvoice().setEditDate(DU.merge(
		  getInvoiceDatePart(), invoiceTimePart
		));
	}

	public boolean isMoveInvoice()
	{
		return Invoices.typeInvoiceMove().equals(getInvoiceType());
	}
}