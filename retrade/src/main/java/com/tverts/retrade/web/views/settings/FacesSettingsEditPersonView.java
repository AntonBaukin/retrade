package com.tverts.retrade.web.views.settings;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth + persons) */

import com.tverts.endure.auth.ActLogin;
import com.tverts.endure.auth.AuthLogin;
import com.tverts.endure.auth.GetAuthLogin;
import com.tverts.endure.person.EditPersonModelBean;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * View of window to edit Person of an Auth Login.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSettingsEditPersonView extends ModelView
{
	/* actions */

	public String doEditPerson()
	{
		//!: update (or create) the person
		actionRun(ActLogin.PERSON, getAuthLogin(),
		  ActLogin.PARAM_PERSON, getModel()
		);

		personUpdated = true;
		return null;
	}


	/* public: view interface */

	public EditPersonModelBean getModel()
	{
		return (EditPersonModelBean) super.getModel();
	}

	public AuthLogin getAuthLogin()
	{
		return (AuthLogin) getModel().accessEntity();
	}

	private boolean personUpdated;

	public boolean isPersonUpdated()
	{
		return personUpdated;
	}


	/* protected: ModelView interface */

	protected EditPersonModelBean createModel()
	{
		EditPersonModelBean mb = new EditPersonModelBean();

		//~: domain of the current user
		mb.setDomain(SecPoint.domain());

		//~: load target instance -> Auth Login
		AuthLogin login = bean(GetAuthLogin.class).
		  getLogin(obtainEntityKeyFromRequestStrict());

		if(login == null) throw EX.state("Entity not an Auth Login!");
		mb.setInstance(login);

		//sec: current domain (or system)
		if(!SecPoint.isSystemDomain())
			if(!SecPoint.domain().equals(login.getDomain().getPrimaryKey()))
				throw EX.state("Auth Login of other Domain!");

		//~: domain of the login
		mb.setDomain(login.getDomain().getPrimaryKey());

		//?: {has person} init it
		if(login.getPerson() != null)
			mb.setPerson(login.getPerson().getOx());

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof EditPersonModelBean);
	}
}