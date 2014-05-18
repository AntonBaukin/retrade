package com.tverts.retrade.web.views.invoices;

/* com.tverts: faces */

import com.tverts.endure.core.GetUnityType;
import com.tverts.faces.ModelViewBase;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: retrade (invoices) */

import com.tverts.retrade.domain.invoice.InvoiceEdit;
import com.tverts.retrade.domain.invoice.InvoiceEditModelBean;
import com.tverts.retrade.domain.invoice.Invoices;

/* com.tverts: retrade (support) */

import com.tverts.support.EX;


/**
 * Implementation base for special controllers
 * for (edited) Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FacesInvoiceSpecialEditBase
       extends        ModelViewBase
{
	/* public: invoice [edit] interface */

	public InvoiceEditModelBean getModel()
	{
		return (model != null)?(model):(model = EX.assertn(
		  findRequestedModelAssignable(InvoiceEditModelBean.class),
		  "Invoice Edit Model is not found!"
		));
	}

	private InvoiceEditModelBean model;

	public InvoiceEdit          getInvoice()
	{
		return getModel().getInvoice();
	}

	private UnityType invoiceType;

	public UnityType            getInvoiceType()
	{
		if(invoiceType != null)
			return invoiceType;

		Long pk = EX.assertn(
		  getInvoice().getInvoiceType(),
		  "Invoice Type is not set in the edit (create) view!"
		);

		return (invoiceType =
		  bean(GetUnityType.class).getUnityType(pk));
	}

	public boolean              isCreating()
	{
		return (getInvoice().objectKey() == null);
	}

	public String               getGotoCode()
	{
		return Invoices.getInvoiceGotoCode(getInvoiceType()) +
		  (isCreating()?("-create"):(""));
	}


}