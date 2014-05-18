package com.tverts.retrade.web.views.selset;

/* standard Java classes */

import java.util.List;

/* JavaServer Faces */

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/* com.tverts: faces */

import com.tverts.faces.ModelView;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: transactions */

import static com.tverts.system.tx.TxPoint.txSession;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: model */

import com.tverts.model.ModelBean;

/* com.tverts: secure */

import com.tverts.secure.SecPoint;

/* com.tverts: endure (auth) */

import com.tverts.endure.auth.GetAuthLogin;

/* com.tverts: retrade domain (selection set) */

import com.tverts.retrade.domain.selset.ActSelSet;
import com.tverts.retrade.domain.selset.GetSelSet;
import com.tverts.retrade.domain.selset.SelSet;
import com.tverts.retrade.domain.selset.SelSetModelBean;
import com.tverts.support.SU;

/* com.tverts: support */

import static com.tverts.support.SU.jss;
import static com.tverts.support.SU.s2s;
import static com.tverts.support.SU.sXe;
import static com.tverts.support.SU.urld;


/**
 * This special Faces Bean is for handling
 * requests to the user Selection Set data.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSelSetView extends ModelView
{
	/* actions */

	public String doAddObject()
	{
		Long   object = obtainEntityKeyFromRequestStrict();
		SelSet selset = getSelSet();

		//!: add to the selection
		actionRun(ActSelSet.ADD, selset, ActSelSet.OBJECTS, object);

		return null;
	}

	public String doDelObject()
	{
		Long   object = obtainEntityKeyFromRequestStrict();
		SelSet selset = getSelSet();

		//!: add to the selection
		actionRun(ActSelSet.CLEAR, selset, ActSelSet.OBJECTS, object);

		return null;
	}

	public String doClearSelSet()
	{
		//!: clear the selection
		actionRun(ActSelSet.CLEAR, getSelSet());

		return null;
	}

	public String doAddSelSet()
	{
		//~: create the selection set
		SelSet set = new SelSet();

		//~: login
		set.setLogin(bean(GetAuthLogin.class).
		  getLogin(getModel().getLogin()));

		//~: select name
		List<String> names = bean(GetSelSet.class).
		  getSelSets(getModel().getLogin());
		int          namei = 1;

		for(String name : names)
			if(name.toLowerCase().matches("выборка \\d+"))
			{
				int i = Integer.parseInt(name.substring("выборка ".length()));
				if(i <= namei)  namei = i + 1;
			}

		//~: set name
		set.setName("Выборка " + namei);

		//!: add the selection set
		actionRun(ActSelSet.ENSURE, set);

		//~: update the view
		this.updatedMenu = true;
		this.renderItems = false;

		return null;
	}

	public String doEditSelSet()
	{
		//?: {this is the default set} skip it
		if(sXe(getModel().getSelSet()))
			return null;

		//?: {the name exists} invalid
		if(isEditNameExists())
			return null;

		//~: create the selection set
		SelSet set = getSelSet();

		//~: update name
		set.setName(getSelSetNameEdit());

		//~: update the model
		getModel().setSelSet(getSelSetNameEdit());
		this.updatedMenu = true;
		this.renderItems = false;

		return null;
	}

	public String doChangeSelSet()
	{
		String selset = s2s(request().getParameter("selset"));
		if(selset == null) selset = "";

		//~: update the model
		getModel().setSelSet(selset);
		this.updatedMenu = true;
		this.renderItems = false;

		return null;
	}

	public String doDeleteSelSet()
	{
		//?: {its is default set} just clear it
		if(sXe(getModel().getSelSet()))
			return doClearSelSet();

		//~: find previous selection set
		List<String> names = bean(GetSelSet.class).
		  getSelSets(getModel().getLogin());

		int i = names.indexOf(getModel().getSelSet());
		if(i == -1) throw new IllegalStateException();

		//!: remove selection set
		SelSet set = getSelSet();
		actionRun(ActSelSet.DELETE, set);

		//~: update the model
		getModel().setSelSet((i > 0)?(names.get(i - 1)):(""));
		this.updatedMenu = true;

		return null;
	}


	/* public: FacesSelSetView interface */

	public SelSetModelBean getModel()
	{
		return (SelSetModelBean) super.getModel();
	}


	/* public: Selection Set window */

	public String getSelSetName()
	{
		String name = s2s(getModel().getSelSet());
		return (name != null)?(name):("По умолчанию");
	}

	public boolean isDefaultSelSet()
	{
		return sXe(getModel().getSelSet());
	}

	public String getWindowTitle()
	{
		return String.format("Выборка '%s'", getSelSetName());
	}


	/* public: Selection Set menu */

	public String getSelSetMenuModel()
	{
		//~: load the names
		List<String> names = bean(GetSelSet.class).
		  getSelSets(getModel().getLogin());

		if(names.isEmpty()) throw new IllegalStateException(String.format(
		  "Login [%d] has no Selection Sets!", getModel().getLogin()
		));

		//~: create JSON object
		StringBuilder s = new StringBuilder(128).append('[');

		for(String name : names)
		{
			if(s.length() != 1) s.append(", ");
			s.append('{');

			//~: title for the default set
			if(sXe(name))
				s.append("title: 'По умолчанию', ");

			//~: name
			s.append("name: '").append(jss(name)).append("', ");

			//~: default
			s.append("current: ").append(getModel().getSelSet().equals(name));

			s.append("}");
		}

		return s.append(']').toString();
	}

	public String getSelSetItems()
	{
		List<Long>    x = bean(GetSelSet.class).
		  getSelItems(getSelSet());

		StringBuilder s = new StringBuilder(x.size() * 8);
		SU.scat(s, ",", x);

		return s.toString();
	}


	/* public: Selection Set edit */

	public boolean isSuccess()
	{
		return !isEditMode() || !isEditNameExists();
	}

	public boolean isEditMode()
	{
		if(editMode == null)
			editMode = "true".equals(request().getParameter("selset-edit"));
		return editMode;
	}

	private Boolean editMode;

	public void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
	}

	public String getSelSetNameEdit()
	{
		if(selSetNameEdit == null)
			selSetNameEdit = getSelSetName();

		return selSetNameEdit;
	}

	private String selSetNameEdit;

	public void setSelSetNameEdit(String name)
	{
		this.selSetNameEdit = s2s(name);
	}

	public boolean isEditNameExists()
	{
		if(editNameExists != null)
			return editNameExists;

		return editNameExists = !sXe(getModel().getModelKey()) &&
		  (bean(GetSelSet.class).getSelSet(
		    getModel().getLogin(),getSelSetNameEdit()) != null);
	}

	private Boolean editNameExists;

	public boolean isUpdatedMenu()
	{
		return updatedMenu;
	}

	private boolean updatedMenu;

	public boolean isRenderItems()
	{
		return renderItems;
	}

	private boolean renderItems = true;


	/* protected: ModelView interface */

	protected SelSetModelBean createModel()
	{
		SelSetModelBean mb = new SelSetModelBean();

		//~: selection of the current user
		mb.setLogin(SecPoint.login());

		//~: domain of the current user
		mb.setDomain(SecPoint.domain());

		//~: selection set parameter
		String selset = urld(s2s(request().getParameter("selset")));

		//?: {has no selection set} take the default name
		mb.setSelSet((selset == null)?(""):(selset));

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SelSetModelBean);
	}


	/* protected: support */

	protected SelSet getSelSet()
	{
		//~: load the set
		SelSet set = bean(GetSelSet.class).
		  getSelSet(getModel().getLogin(), getModel().getSelSet());

		//~: the set is not found
		if(set == null) throw new IllegalStateException(String.format(
		  "Selection Set named [%s] not found for user [%d]!",
		  getModel().getSelSet(), getModel().getLogin()
		));

		return set;
	}
}