package com.tverts.retrade.web.views.invoices;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: retrade (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: retrade (support) */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Special controller for shared implementation
 * of (edited) Invoice edit Contractor page.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class   FacesInvoiceEditFirm
       extends FacesInvoiceSpecialEditBase
{
	/* actions */

	public String gotoCancelEdit()
	{
		return "done-edit-" + getGotoCode();
	}

	public String doEditContractor()
	{
		//~: load the contractor
		String     p = SU.s2s(request().getParameter("contractor"));
		Contractor c = EX.assertn( bean(GetContractor.class).
		  getContractor((p == null)?(null):Long.valueOf(p)),
		  "Contractor [", p, "] is not found!"
		);

		//sec: contractor of the same domain
		if(!c.getDomain().getPrimaryKey().equals(getModel().getDomain()))
			throw EX.forbid();

		//~: assign it
		getInvoice().setContractor(c.getPrimaryKey());
		return "done-edit-" + getGotoCode();
	}

	public String doSearchFirms()
	{
		//~: search names
		getModel().setContractorsSearch(
		  SU.s2s(request().getParameter("searchFirms"))
		);

		//~: selection set
		getModel().setContractorsSelSet(
		  request().getParameter("selset"));

		return null;
	}


	/* public: invoice [edit contractor] interface */

	public String getWindowTitleEditFirm()
	{
		return formatTitle("Выбор контрагента накладной",
		  getInvoice().getInvoiceCode());
	}
}