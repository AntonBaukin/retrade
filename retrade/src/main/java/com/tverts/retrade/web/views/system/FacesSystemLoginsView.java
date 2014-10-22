package com.tverts.retrade.web.views.system;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.AuthLogin;

/* com.tverts: endure domain (auth) */

import com.tverts.retrade.domain.auth.AuthLoginsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * The view of {@link AuthLogin}s list table
 * of the Domain specified.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSystemLoginsView extends ModelView
{
	/* public: actions */

	public String doSearchLogins()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("selset")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}

	public String doSetCurrentLogin()
	{
		//~: read current login parameter
		String p = SU.s2s(request().getParameter("currentLogin"));
		if(p == null) return null;
		currentLogin = Long.parseLong(p);

		//sec: current login in the domain | system domain
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(getCurrentLogin().getDomain().getPrimaryKey()))
				throw EX.forbid("Auth Login of else Domain!");

		return null;
	}


	/* public: view interface */

	public AuthLoginsModelBean getModel()
	{
		return (AuthLoginsModelBean) super.getModel();
	}

	public String getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}

	public String getWindowTitle()
	{
		Domain d = bean(GetDomain.class).
		  getDomain(getModel().domain());

		return String.format(
		  "Пользователи домена %s '%s'",
		  d.getCode(), d.getName()
		);
	}

	private Long currentLogin;

	public boolean isSetCurrentLogin()
	{
		return (currentLogin != null);
	}

	private AuthLogin authLogin;

	public AuthLogin getCurrentLogin()
	{
		return (authLogin != null)?(authLogin):(currentLogin == null)?(null):
		  (authLogin = bean(GetAuthLogin.class).getLogin(currentLogin));
	}

	public boolean isSecureChangeCurrentLogin()
	{
		return (getCurrentLogin() != null) &&
		  isSecureEntity(getCurrentLogin(), "secure: operate: change login");
	}

	public boolean isSecureChangeCurrentPassword()
	{
		return (getCurrentLogin() != null) &&
		  isSecureEntity(getCurrentLogin(), "secure: operate: change password");
	}

	public boolean isSecureViewCurrentAbles()
	{
		return (getCurrentLogin() != null) &&
		  isSecureEntity(getCurrentLogin(), "secure: view: ables");
	}


	/* protected: ModelView interface */

	protected AuthLoginsModelBean createModel()
	{
		AuthLoginsModelBean mb = new AuthLoginsModelBean();

		//~: requested domain
		mb.setDomain(getTargetDomain());

		return mb;
	}

	protected Long getTargetDomain()
	{
		Long domain = obtainEntityKeyFromRequestStrict();

		//?: {not a Domain}
		if(bean(GetDomain.class).getDomain(domain) == null)
			throw new IllegalArgumentException();

		return domain;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof AuthLoginsModelBean);
	}
}