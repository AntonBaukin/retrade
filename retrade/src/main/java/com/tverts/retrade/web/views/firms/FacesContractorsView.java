package com.tverts.retrade.web.views.firms;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.ContractorsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of the general contractors table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesContractorsView extends ModelView
{
	/* actions */

	public String doSearchContractors()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("searchNames")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}


	/* Faces Contractors View */

	public ContractorsModelBean    getModel()
	{
		return (ContractorsModelBean)super.getModel();
	}


	/* protected: ModelView interface */

	protected ContractorsModelBean createModel()
	{
		ContractorsModelBean mb = new ContractorsModelBean();

		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean              isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ContractorsModelBean);
	}
}