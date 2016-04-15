package com.tverts.retrade.web.views.settings;

/* standard Java classes */

import java.security.SecureRandom;
import java.util.HashMap;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.Auth;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.ActLogin;
import com.tverts.endure.auth.EditPasswordModelBean;
import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: support */

import com.tverts.support.AU;
import com.tverts.support.EX;
import com.tverts.support.SU;
import static com.tverts.support.SU.s2s;


/**
 * View of window to edit login and password.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSettingsPasswordView extends ModelView
{
	/* actions */

	public String doCheckLoginExists()
	{
		String    login = s2s(request().getParameter("login"));
		AuthLogin found = (login == null)?(null):(bean(GetAuthLogin.class).
		  getLogin(getModel().getLoginDomain(), login));

		//sec: forbid 'System' in any case
		loginExists = Auth.SYSTEM_USER.equalsIgnoreCase(login);

		//?: {creating}
		if(!loginExists) if(getModel().isCreating())
			loginExists = (found != null);
		else
			loginExists = (found != null) && !getAuthLogin().equals(found);

		valid = !loginExists;
		return null;
	}

	@SuppressWarnings("unchecked")
	public String doSetLoginAndPassword()
	{
		//~: check the login
		doCheckLoginExists();

		//?: {login check failed}
		if(!valid) return null;

		//~: {change the password also}
		String P0 = s2s(request().getParameter("P0"));
		if(P0 != null)
		{
			//sec: check password of the active user
			String Px = s2s(request().getParameter("Px"));
			if(Px == null) throw EX.state("Has no Px parameter!");

			String P  = SecPoint.loadLogin().getPasshash();
			String Xx = Auth.passwordHash(null, P);
			if(!Px.toUpperCase().equalsIgnoreCase(Xx))
			{
				wrongUserPassword = true; valid = false;
				return null;
			}

			//~: calculate the password hash
			byte[] px = SU.hex2bytes(P);
			byte[] p0 = SU.hex2bytes(P0);
			if((px.length != 20) || (p0.length != 20)) throw EX.state();
			AU.xor(p0, px);
			P0 = new String(SU.bytes2hex(p0));
		}

		String loginCode = s2s(request().getParameter("login"));

		//?: {creating} do create
		if(getModel().isCreating())
		{
			AuthLogin login = new AuthLogin();

			//~: domain
			login.setDomain(bean(GetDomain.class).
			  getDomain(getModel().getLoginDomain()));

			//~: login code
			login.setCode(loginCode);

			//~: empty name
			login.setName("Новый пользователь");

			//?: {has user-defined password}
			if(P0 != null)
				login.setPasshash(P0);
			//~: create random password
			else
			{
				SecureRandom rnd = new SecureRandom();
				byte[]       b   = new byte[20];

				rnd.setSeed(rnd.generateSeed(8 +
				  (int)(System.currentTimeMillis() & 0x07)));

				rnd.nextBytes(b);
				login.setPasshash(new String(SU.bytes2hex(b)));
			}

			//~: create time
			login.setCreateTime(new java.util.Date());

			//~: description
			login.setDescr("Данный пользователь был недавно создан.\n" +
			  "Необходимо отредактировать основные параметры!");

			//!: do save
			actionRun(ActLogin.SAVE, login);

			//~: update the model
			getModel().setAuthLogin(login.getPrimaryKey());
			getModel().setLoginCode(login.getCode());
		}
		//!: update the login and password
		else
		{
			HashMap p = new HashMap(2);

			if(!isLoginDisabled())
				p.put(ActLogin.PARAM_LOGIN, loginCode);

			if(!isPasswordDisabled())
				p.put(ActLogin.PARAM_PASSHASH, P0);

			//?: {has at least one of two enabled}
			if(!p.isEmpty())
				actionRun(ActLogin.PASSWORD, getAuthLogin(), p);
		}

		return null;
	}


	/* public: view interface */

	public EditPasswordModelBean getModel()
	{
		return (EditPasswordModelBean) super.getModel();
	}

	private AuthLogin authLogin;

	public AuthLogin getAuthLogin()
	{
		return (authLogin != null)?(authLogin):
		  (authLogin = bean(GetAuthLogin.class).getLogin(getModel().getAuthLogin()));
	}

	private boolean valid = true;

	public boolean isValid()
	{
		return valid;
	}

	private boolean loginExists;

	public boolean isLoginExists()
	{
		return loginExists;
	}

	public boolean isWrongUserPassword()
	{
		return wrongUserPassword;
	}

	private boolean wrongUserPassword;

	public void forceCheckSecure()
	{
		if(getModel().isCreating())
			forceSecure("secure: create: person login");
		else
			forceAnySecureEntity(getAuthLogin(),
			  "secure: operate: change login; secure: operate: change password");
	}

	public boolean isLoginDisabled()
	{
		return !getModel().isCreating() &&
		  !isSecureEntity(getAuthLogin(), "secure: operate: change login");
	}

	public boolean isPasswordDisabled()
	{
		return !getModel().isCreating() &&
		  !isSecureEntity(getAuthLogin(), "secure: operate: change password");
	}


	/* protected: ModelView interface */

	protected EditPasswordModelBean createModel()
	{
		EditPasswordModelBean mb = new EditPasswordModelBean();

		//~: domain of the current user
		mb.setDomain(SecPoint.domain());

		//~: default login domain
		mb.setLoginDomain(mb.getDomain());

		//~: creating flag
		mb.setCreating("true".equals(
		  request().getParameter("creating")));

		//?: {edit mode}
		if(!mb.isCreating())
		{
			//~: auth login to edit
			Long      entity = obtainEntityKeyFromRequestStrict();
			mb.setAuthLogin(entity);

			AuthLogin login  = bean(GetAuthLogin.class).getLogin(entity);
			if(login == null) throw EX.state("Not an Auth Login!");

			//sec: current domain (or system)
			if(!SecPoint.isSystemDomain())
				if(!SecPoint.domain().equals(login.getDomain().getPrimaryKey()))
					throw EX.state("Auth Login of other Domain!");

			//~: login domain
			mb.setLoginDomain(login.getDomain().getPrimaryKey());

			//~: current login code value
			mb.setLoginCode(login.getCode());
		}

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof EditPasswordModelBean);
	}
}