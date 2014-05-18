package com.tverts.retrade.web.views.settings;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.retrade.secure.SecRulesModelBean;
import com.tverts.secure.SecPoint;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.core.GetDomain;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * View of Secure Forces and Rules of the Domain.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSettingsSecureRulesView extends ModelView
{
	/* public: actions */

	public String doSearchRules()
	{
		String[] sestr  = null;
		String   seprm  = request().getParameter("searchRules");
		String   selset = request().getParameter("selset");

		//~: search names
		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");
		getModel().setSearchNames(sestr);

		//~: selection set
		getModel().setSelSet(selset);

		return null;
	}


	/* public: view interface */

	public SecRulesModelBean getModel()
	{
		return (SecRulesModelBean) super.getModel();
	}

	public String getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}


	/* protected: ModelView interface */

	protected SecRulesModelBean createModel()
	{
		SecRulesModelBean mb = new SecRulesModelBean();

		//~: user | requested domain
		mb.setDomain(getTargetDomain());

		return mb;
	}

	protected Long getTargetDomain()
	{
		//?: {not a system domain} take of the user
		if(!SecPoint.isSystemDomain())
			return SecPoint.domain();

		Long domain = obtainEntityKeyFromRequestStrict();

		//?: {not a Domain}
		if(bean(GetDomain.class).getDomain(domain) == null)
			throw new IllegalArgumentException();

		return domain;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SecRulesModelBean);
	}
}