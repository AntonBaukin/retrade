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

import com.tverts.retrade.domain.account.PayWaysModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of the Payment Ways table.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesPayWaysView extends ModelView
{
	/* actions */

	public String doSearchPayWays()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().
		  getParameter("searchNames")));

		//~: with own payment ways only
		getModel().setOwnOnly("true".equals(request().
		  getParameter("ownPayWaysOnly")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}


	/* public: view interface */

	public PayWaysModelBean getModel()
	{
		return (PayWaysModelBean) super.getModel();
	}


	/* protected: ModelView interface */

	protected PayWaysModelBean createModel()
	{
		PayWaysModelBean mb = new PayWaysModelBean();
		mb.setDomain(getDomainKey());
		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof PayWaysModelBean);
	}
}