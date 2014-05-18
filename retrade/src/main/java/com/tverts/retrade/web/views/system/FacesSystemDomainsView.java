package com.tverts.retrade.web.views.system;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.core.DomainsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * The view of Domains list table on the
 * System Domain desktop.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSystemDomainsView extends ModelView
{
	/* public: actions */

	public String doSearchDomains()
	{
		String[] sestr = null;
		String   seprm = request().getParameter("searchDomains");

		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");

		getModel().setSearchNames(sestr);
		return null;
	}


	/* public: FacesSystemDomainsView (bean) interface */

	public DomainsModelBean getModel()
	{
		return (DomainsModelBean) super.getModel();
	}

	public String           getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}


	/* protected: ModelView interface */

	protected DomainsModelBean createModel()
	{
		return new DomainsModelBean();
	}

	protected boolean          isRequestModelMatch(ModelBean model)
	{
		return (model instanceof DomainsModelBean);
	}
}