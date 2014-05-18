package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


/**
 * Back bean for edit model of Buy Invoices.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesInvoiceBuyEdit
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
}