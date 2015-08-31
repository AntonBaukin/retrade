package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Back bean for edit model of Volume Check Invoices.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesVolumeCheckEdit
       extends FacesInvoiceEditBase
{
	/* public: [edit] interface */

	public String getWinmainTitleEdit()
	{
		return formatTitle("Ред. инв. остатков",
		  getInvoice().getInvoiceCode(),
		  DU.datetime2str(getInvoice().getInvoiceDate())
		);
	}
}