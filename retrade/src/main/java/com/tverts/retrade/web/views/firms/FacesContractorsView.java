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
		this.setSearchNames(SU.s2s(request().getParameter("searchNames")));
		return null;
	}


	/* public: FacesContractorsView (bean) interface */

	public ContractorsModelBean    getModel()
	{
		return (ContractorsModelBean)super.getModel();
	}


	/* public: FacesContractorsView (view) interface */

	public String getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}

	public void   setSearchNames(String names)
	{
		getModel().setSearchNames(SU.s2a(names));
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