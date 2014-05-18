package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


/**
 * Back bean for edit model of Sell Invoices.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceSellEdit
       extends FacesInvoiceEditBase
{
	/* protected: actions support */

	protected boolean contractorRequired()
	{
		return true;
	}

	protected boolean goodVolumeCostRequired()
	{
		return true;
	}


	/* public: [edit] interface */

	public boolean isAltered()
	{
		return getInvoice().isAltered();
	}

	public void    setAltered(boolean a)
	{
		getInvoice().setSubType(a?('A'):(null));
	}
}