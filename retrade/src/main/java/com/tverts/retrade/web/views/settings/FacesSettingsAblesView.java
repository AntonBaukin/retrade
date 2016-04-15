package com.tverts.retrade.web.views.settings;

/* standard Java classes */

import java.util.List;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth + secure) */

import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.secure.ActSecSet;
import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecAble;
import com.tverts.endure.secure.SecSet;

/* com.tverts: retrade secure */

import com.tverts.retrade.secure.SecAblesModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;
import com.tverts.support.fmt.FmtPoint;


/**
 * View of window with Secure Ables table
 * of the Auth Login given.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSettingsAblesView extends ModelView
{
	/* public: actions */

	public String doSearchRules()
	{
		//~: search names
		getModel().setSearchNames(SU.s2s(request().getParameter("searchAbles")));

		//~: selection set
		getModel().setSelSet(request().getParameter("selset"));

		return null;
	}

	public String doGrantSelectedRulesAndSets()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: take the rules selected
		GetSecure  get   = bean(GetSecure.class);
		List<Long> rules = get.findSelectedRules(
		  getAuthLogin().getDomain().getPrimaryKey(), selset
		);

		//~: ensure them (with the default set)
		for(Long rule : rules)
		{
			SecAble able = new SecAble();

			//~: able login
			able.setLogin(getAuthLogin());

			//~: able rule
			able.setRule(get.getSecRule(rule));


			//!: ensure it
			actionRun(ActionType.ENSURE, able);
		}

		//~: take the secure sets selected
		List<SecSet> sets = get.findSelectedSets(
		  getAuthLogin().getDomain().getPrimaryKey(), selset
		);

		//~: grant the sets found
		for(SecSet set : sets)
			actionRun(ActSecSet.GRANT, set,
			  ActSecSet.LOGIN, getAuthLogin());

		return null;
	}

	public String doRevokeSelectedAbles()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		String[]  pkeys  = SU.a2a(request().getParameterValues("secAbles"));
		GetSecure get    = bean(GetSecure.class);

		//c: for all the keys given: load and check
		for(String pkey : pkeys)
		{
			//~: load the able
			SecAble able = get.getSecAble(Long.parseLong(pkey));
			if(able == null) throw EX.state("Secure Able [", pkey, "] not found!");

			//~: the domain of the able
			Long domain = able.getRule().getDomain().getPrimaryKey();

			//sec: check domain of the set
			if(!getAuthLogin().getDomain().getPrimaryKey().equals(domain))
				throw EX.forbid("Secure Able of not that Login!");

			//!: do delete this prototype able
			actionRun(ActionType.DELETE, able);
		}

		return null;
	}

	public String doRevokeAblesOfDefaultSet()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: get the default set
		GetSecure get = bean(GetSecure.class);
		SecSet    set = get.getDefaultSecSet(
		  getAuthLogin().getDomain().getPrimaryKey()
		);

		//~: find the ables of the rules selected in the default set
		List<SecAble> ables = get.findLoginAblesOfSelectedRules(
		  getAuthLogin().getPrimaryKey(), set.getPrimaryKey(), selset
		);

		//~: delete them
		for(SecAble able : ables)
			actionRun(ActionType.DELETE, able);

		return null;
	}

	public String doRevokeAblesOfAllSets()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: find the ables of the rules selected in all the sets
		List<SecAble> ables = bean(GetSecure.class).
		  findLoginAblesOfSelectedRules(getAuthLogin().getPrimaryKey(), selset);

		//~: delete them
		for(SecAble able : ables)
			actionRun(ActionType.DELETE, able);

		return null;
	}

	public String doRevokeSelectedSets()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: take the sets selected
		List<SecSet> sets = bean(GetSecure.class).
		  findSelectedSets(getAuthLogin().getDomain().getPrimaryKey(), selset);

		//c: for all the sets: revoke
		for(SecSet set : sets)
			actionRun(ActSecSet.REVOKE, set, ActSecSet.LOGIN, getAuthLogin());

		return null;
	}


	/* public: view interface */

	public SecAblesModelBean getModel()
	{
		return (SecAblesModelBean) super.getModel();
	}

	private AuthLogin loginCached;

	public AuthLogin getAuthLogin()
	{
		if(loginCached != null)
			return loginCached;

		return loginCached = bean(GetAuthLogin.class).
		  getLogin(getModel().getAuthLogin());
	}

	public String getLoginTitle()
	{
		return FmtPoint.format(getAuthLogin(), FmtPoint.CODE);
	}

	public String getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}


	/* protected: ModelView interface */

	protected SecAblesModelBean createModel()
	{
		SecAblesModelBean mb = new SecAblesModelBean();

		//~: load the login given
		AuthLogin login = bean(GetAuthLogin.class).
		  getLogin(obtainEntityKeyFromRequestStrict());
		if(login == null) throw EX.state("Not an Auth Login!");

		//sec: current domain (or system)
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(login.getDomain().getPrimaryKey()))
				throw EX.state("Auth Login of other Domain!");

		//~: assign the login
		mb.setAuthLogin(login.getPrimaryKey());

		//~: domain of the login
		mb.setDomain(login.getDomain().getPrimaryKey());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SecAblesModelBean);
	}
}