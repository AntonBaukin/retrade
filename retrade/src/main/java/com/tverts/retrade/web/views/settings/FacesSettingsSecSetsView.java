package com.tverts.retrade.web.views.settings;

/* standard Java classes */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Spring Framework */

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* com.tverts: servlets */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;
import com.tverts.retrade.secure.SecAblesModelBean;

/* com.tverts: endure (core + auth + secure) */

import com.tverts.endure.core.GetDomain;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.secure.ActSecSet;
import com.tverts.endure.secure.GetSecure;
import com.tverts.endure.secure.SecAble;
import com.tverts.endure.secure.SecSet;

/* com.tverts: retrade secure */

import com.tverts.retrade.secure.SecSetsModelBean;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * View of the Secure Sets.
 *
 * @author anton.baukin@gmail.com
 */
@Component("facesSettingsSecSetsView") @Scope("request")
public class FacesSettingsSecSetsView extends ModelView
{
	/* public: actions */

	public String doSearchSets()
	{
		String[] sestr  = null;
		String   seprm  = request().getParameter("searchSets");
		String   selset = request().getParameter("selset");

		//~: search names
		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");
		getModel().setSearchNames(sestr);

		//~: selection set
		getModel().setSelSet(selset);

		return null;
	}

	public String doSearchSetAbles()
	{
		String[] sestr  = null;
		String   seprm  = request().getParameter("searchAbles");
		String   selset = request().getParameter("selset");

		//~: search names
		if(!SU.sXe(seprm))
			sestr = SU.s2s(seprm).split("\\s+");
		getSetAblesModel().setSearchNames(sestr);

		//~: selection set
		getSetAblesModel().setSelSet(selset);

		return null;
	}

	public String doAddSet()
	{
		//sec: force operation secure
		forceSecure("secure: operate: sets");

		String name = SU.s2s(request().getParameter("setName"));
		if(name == null) return null;

		SecSet set  = new SecSet();

		//~: domain
		set.setDomain(bean(GetDomain.class).
		  getDomain(getModel().domain())
		);

		//~: name
		set.setName(name);

		//!: ensure the secure set
		actionRun(ActionType.ENSURE, set);

		return null;
	}

	public String doDeleteSet()
	{
		//sec: force operation secure
		forceSecure("secure: operate: sets");

		String pkey = SU.s2s(request().getParameter("secSet"));
		SecSet set  = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't delete the default Secure Set!");


		//!: delete the secure set
		actionRun(ActionType.DELETE, set);

		return null;
	}

	public String doEditSetName()
	{
		//sec: force operation secure
		forceSecure("secure: operate: sets");

		//~: get the name
		String name = SU.s2s(request().getParameter("setName"));
		if(name == null) throw EX.state("Can't assign empty name to Secure Set!");

		//~: load the secure set
		String pkey = SU.s2s(request().getParameter("secSet"));
		SecSet set  = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't change the name of a default Secure Set!");

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(set.getDomain().getPrimaryKey()))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {already has set with this name}
		SecSet exist = bean(GetSecure.class).
		  getSecSet(set.getDomain().getPrimaryKey(), name);
		if((exist != null) && !exist.getPrimaryKey().equals(set.getPrimaryKey()))
			return null;


		//!: do in-place edit
		set.setName(name);

		return null;
	}

	public String doCommentSet()
	{
		//~: get the name
		String comment = SU.s2s(request().getParameter("setComment"));

		//~: load the secure set
		String pkey = SU.s2s(request().getParameter("secSet"));
		SecSet set  = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't comment the default Secure Set!");

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(set.getDomain().getPrimaryKey()))
				throw EX.forbid("Secure Set of wrong Domain!");

		//!: do in-place edit
		set.setComment(comment);

		return null;
	}

	public String doChangeCurrentSet()
	{
		//~: load the secure set
		String pkey = SU.s2s(request().getParameter("secSet"));
		SecSet set  = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(set.getDomain().getPrimaryKey()))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't set the default Secure Set as current!");

		//!: update the related model
		getSetAblesModel().setSecSet(set.getPrimaryKey());

		return null;
	}

	public String doAddRulesToCurrentSet()
	{
		//sec: force operation secure
		forceSecure("secure: operate: sets");

		//~: load the secure set
		String pkey   = SU.s2s(request().getParameter("secSet"));
		SecSet set    = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");
		Long   domain = set.getDomain().getPrimaryKey();

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(domain))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't add rules to the default Secure Set!");

		//~: selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//<: collect the rules to add to the set
		GetSecure get   = bean(GetSecure.class);
		Set<Long> rules = new HashSet<Long>(11);

		rules.addAll(get.findSelectedRules(domain, selset));
		rules.addAll(get.findSelectedSetsRules(domain, selset));
		rules.addAll(get.findSelectedLoginsRules(domain, selset));

		//?: {has nothing} do nothing
		if(rules.isEmpty()) return null;

		//>: collect the rules to add to the set


		Long system = bean(GetAuthLogin.class).
		  getSystemLoginStrict(domain).getPrimaryKey();


		//~: remove the rules already in the set
		List<Long> exist = get.findSetAbleRules(system, set.getPrimaryKey());
		rules.removeAll(exist);

		//~: ensure (save) new rules
		AuthLogin l = bean(GetAuthLogin.class).getLogin(system);

		for(Long r : rules)
		{
			SecAble a = new SecAble();

			//~: login
			a.setLogin(l);

			//~: rule
			a.setRule(get.getSecRule(r));

			//~: secure set
			a.setSet(set);

			//!: save the able
			actionRun(ActionType.SAVE, a);
		}

		return null;
	}

	public String doDeleteSelectedRules()
	{
		//sec: force operation secure
		forceSecure("secure: operate: sets");

		String[]  pkeys  = SU.a2a(request().getParameterValues("secAbles"));
		GetSecure get    = bean(GetSecure.class);
		Long      syslo  = null;

		//c: for all the keys given: load and check
		for(String pkey : pkeys)
		{
			//~: load the able
			SecAble able = get.getSecAble(Long.parseLong(pkey));
			if(able == null) throw EX.state("Secure Able [", pkey, "] not found!");

			//~: the domain of the able
			Long domain = able.getRule().getDomain().getPrimaryKey();

			//~: load system login
			if(syslo == null)
				syslo = bean(GetAuthLogin.class).
				  getSystemLoginStrict(domain).getPrimaryKey();

			//~: sets from differ domains
			if(!able.getLogin().getPrimaryKey().equals(syslo))
				throw EX.state("Secure Able [", pkey, "] not a Secure Set prototype!");


			//!: do delete this prototype able
			actionRun(ActionType.DELETE, able);
		}

		return null;
	}

	public String doGrantSetToSelSetUsers()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: load the secure set
		String pkey   = SU.s2s(request().getParameter("secSet"));
		SecSet set    = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");
		Long   domain = set.getDomain().getPrimaryKey();

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(domain))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't grant the default Secure Set to users!");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: load the logins of current selection set
		List<AuthLogin> logins = bean(GetAuthLogin.class).
		  getSelectedLogins(set.getDomain().getPrimaryKey(), selset);

		//c: for all the logins: grant
		for(AuthLogin login : logins)
			actionRun(ActSecSet.GRANT, set, ActSecSet.LOGIN, login);

		return null;
	}

	public String doSynchSetWithSelSetUsers()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: load the secure set
		String pkey   = SU.s2s(request().getParameter("secSet"));
		SecSet set    = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");
		Long   domain = set.getDomain().getPrimaryKey();

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(domain))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't synchronize the default Secure Set with users!");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: take the logins selected
		List<AuthLogin> logins = bean(GetAuthLogin.class).
		  getSelectedLogins(set.getDomain().getPrimaryKey(), selset);

		//c: for all the able logins: synchronize
		GetSecure get = bean(GetSecure.class);
		for(AuthLogin login : logins)
		{
			//~: find the ables of the set
			List<Long> ables = get.findSetAbleRules(
			  login.getPrimaryKey(), set.getPrimaryKey()
			);

			//?: {has no ables} skip this login
			if(ables.isEmpty()) continue;

			//!: synchronize
			actionRun(ActSecSet.SYNCH, set, ActSecSet.LOGIN, login);
		}

		return null;
	}

	public String doDeleteSetFromSelSetUsers()
	{
		//sec: force operation secure
		forceSecure("secure: operate: rules");

		//~: load the secure set
		String pkey   = SU.s2s(request().getParameter("secSet"));
		SecSet set    = bean(GetSecure.class).getSecSet(Long.parseLong(pkey));
		if(set == null) throw EX.state("Secure Set [", pkey, "] not found!");
		Long   domain = set.getDomain().getPrimaryKey();

		//sec: check domain of the set
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(domain))
				throw EX.forbid("Secure Set of wrong Domain!");

		//?: {the default set}
		if(SU.sXe(set.getName()))
			throw EX.state("Can't delete the default Secure Set from users!");

		//~: current selection set
		String selset = request().getParameter("selset");
		if(SU.sXe(selset)) selset = "";

		//~: take the logins selected
		List<AuthLogin> logins = bean(GetAuthLogin.class).
		  getSelectedLogins(set.getDomain().getPrimaryKey(), selset);

		//c: for all the logins: revoke
		for(AuthLogin login : logins)
			actionRun(ActSecSet.REVOKE, set, ActSecSet.LOGIN, login);

		return null;
	}


	/* public: ModelProvider interface */

	/**
	 * In this view model provider is used solely
	 * to access {@link #getSetAblesModel()}.
	 */
	public ModelBean provideModel()
	{
		return getSetAblesModel();
	}


	/* public: view interface */

	public SecSetsModelBean getModel()
	{
		return (SecSetsModelBean) super.getModel();
	}

	public SecAblesModelBean getSetAblesModel()
	{
		if(this.secAblesModel != null)
			return this.secAblesModel;

		//~: load the model from the store
		SecAblesModelBean samb = (SecAblesModelBean) modelPoint().
		  readBean(getModel().getAblesModel());

		//?: {lost it} create the new one
		if(samb == null)
			samb = createAblesModel(getModel());

		return this.secAblesModel = samb;
	}

	private SecAblesModelBean secAblesModel;

	public String getSearchNames()
	{
		return SU.a2s(getModel().getSearchNames());
	}

	public String getSearchSetAblesNames()
	{
		return SU.a2s(getSetAblesModel().getSearchNames());
	}


	/* protected: ModelView interface */

	protected SecSetsModelBean createModel()
	{
		//~: create model for the user Domain
		SecSetsModelBean mb = new SecSetsModelBean();

		//~: restrict to persons only
		mb.setDomain(SecPoint.domain());

		//~: create model bean for secure set rules
		SecAblesModelBean samb = createAblesModel(mb);
		if(samb.getModelKey() == null) throw EX.state();
		mb.setAblesModel(samb.getModelKey());
		this.secAblesModel = samb;

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SecSetsModelBean);
	}

	protected SecAblesModelBean createAblesModel(SecSetsModelBean mb)
	{
		//HINT: secure set rules are defined as ables
		//  of System user. Here we create ables model
		//  to select them.

		SecAblesModelBean samb = new SecAblesModelBean();

		//~: domain
		samb.setDomain(mb.domain());

		//~: System user login
		samb.setAuthLogin(bean(GetAuthLogin.class).
		  getSystemLoginStrict(mb.domain()).getPrimaryKey()
		);

		//?: {sets model has the key for this model}
		if(mb.getModelKey() != null)
			samb.setModelKey(mb.getModelKey());

		//!: save this model bean
		modelPoint().addBean(samb);

		return samb;
	}
}