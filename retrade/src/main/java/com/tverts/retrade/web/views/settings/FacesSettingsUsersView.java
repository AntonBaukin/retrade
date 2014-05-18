package com.tverts.retrade.web.views.settings;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: retrade domain (auth) */

import com.tverts.retrade.domain.auth.AuthLoginsModelBean;

/* com.tverts: faces views (system) */

import com.tverts.retrade.web.views.system.FacesSystemLoginsView;

/* com.tverts: support */

import com.tverts.support.EX;
import static com.tverts.support.SU.s2s;


/**
 * View of the regular Domain Logins
 * (of the Persons only).
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSettingsUsersView extends FacesSystemLoginsView
{
	/* actions */

	public String doSetLoginComment()
	{
		Long      pk = obtainEntityKeyFromRequestStrict();
		AuthLogin lo = bean(GetAuthLogin.class).getLogin(pk);

		//sec: login of the same domain
		if(!SecPoint.domain().equals(lo.getDomain().getPrimaryKey()))
			throw EX.state("Login from another Domain!");

		//~: update the description (comment)
		lo.setDescr(s2s(request().getParameter("comment")));

		return null;
	}


	/* protected: ModelView interface */

	protected AuthLoginsModelBean createModel()
	{
		//~: create model for the user Domain
		AuthLoginsModelBean mb = super.createModel();

		//~: restrict to persons only
		mb.setPersonsOnly(true);

		return mb;
	}

	protected Long getTargetDomain()
	{
		return SecPoint.domain();
	}
}