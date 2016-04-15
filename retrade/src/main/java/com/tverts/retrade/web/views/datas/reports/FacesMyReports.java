package com.tverts.retrade.web.views.datas.reports;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.param;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Faces back bean for Report Requests table
 * created and available to download for a user.
 *
 * @author anton.baukin@gmail.com.
 */
@ManagedBean @RequestScoped
public class FacesMyReports extends ModelView
{
	/* actions */

	public String doSearchReports()
	{
		//~: search words
		getModel().setSearchNames(SU.s2s(param("searchWords")));

		return null;
	}


	/* protected: ModelView interface */

	public ReportRequestsModelBean getModel()
	{
		return (ReportRequestsModelBean) super.getModel();
	}

	protected ReportRequestsModelBean createModel()
	{
		ReportRequestsModelBean mb = new ReportRequestsModelBean();

		//~: domain
		mb.setDomain(getDomainKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof ReportRequestsModelBean);
	}
}