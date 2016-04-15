package com.tverts.retrade.web.views.accounts;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (accounts) */

import com.tverts.retrade.domain.account.AccountsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of the Accounts table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesAccountsView extends ModelView
{
	/* actions */

	public String doSearchAccounts()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().
		  getParameter("searchNames")));

		//~: with own accounts only
		getModel().setOwnOnly("true".equals(request().
		  getParameter("ownAccountsOnly")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}


	/* public: view interface */

	public AccountsModelBean getModel()
	{
		return (AccountsModelBean) super.getModel();
	}


	/* protected: ModelView interface */

	protected AccountsModelBean createModel()
	{
		AccountsModelBean mb = new AccountsModelBean();
		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof AccountsModelBean);
	}
}