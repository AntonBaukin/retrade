package com.tverts.retrade.web.views.system;

/* standard Java classes */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.isTestPrimaryKey;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: shunts */

import com.tverts.shunts.SelfShuntGroupsModelBean;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.service.SelfShuntGroupsEvent;
import com.tverts.system.services.ServicesPoint;

/* com.tverts: support */

import static com.tverts.support.DU.timefull2str;
import static com.tverts.support.SU.s2s;


/**
 * Faces view to Self-Shunt Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSystemSelfShuntView extends ModelView
{
	/* actions */

	public String doSelfShunt()
	{
		//~: find groups set
		Set<String>  groups = new HashSet<String>();
		final String G      = "selfShuntGroup_";

		//~: get groups from the request
		for(String pn : request().getParameterMap().keySet())
			if(pn.startsWith(G))
			{
				String v = s2s(request().getParameter(pn));
				if("true".equals(v))
					groups.add(pn.substring(G.length()));
			}

		if(groups.isEmpty())
			throw new IllegalStateException(
			  "No Self-Shunt groups are selected!");

		//~: create shunt event
		SelfShuntGroupsEvent event = new SelfShuntGroupsEvent();

		//~: the domain to shunt
		event.setDomain(getModel().getDomain());

		//~: groups
		event.getGroups().addAll(groups);

		//~: read-only flag
		event.setReadonly(!getModel().isUpdating());

		//~: log parameter
		String log = "Log " + timefull2str(null);
		getModel().setLogParam(log);
		event.setLogParam(log);

		//!: send event for background execution
		ServicesPoint.send(
		  SelfShuntPoint.getInstance().getService().uid(),
		  event
		);

		return null;
	}

	public String gotoSelfShuntLog()
	{
		return "done";
	}

	public String doGetSelfShuntLog()
	{
		//~: find the domain creating
		Domain domain = bean(GetDomain.class).
		  getDomain(getModel().getDomain());

		//?: {not found} get the system domain as error fallback
		if(domain == null)
			throw new IllegalStateException();

		//~: load the log property
		GetProps get = bean(GetProps.class);
		Property log = get.get(domain, "Self-Shunt", getModel().getLogParam());

		if(log != null)
			selfShuntLog = get.string(log);

		return null;
	}


	/* public: view interface */

	public SelfShuntGroupsModelBean getModel()
	{
		return (SelfShuntGroupsModelBean) super.getModel();
	}

	public String getWindowTitle()
	{
		Domain d = bean(GetDomain.class).
		  getDomain(getModel().domain());

		return String.format(
		  "Тестирование домена %s '%s'",
		  d.getCode(), d.getName()
		);
	}

	public String getLogWindowTitle()
	{
		Domain d = bean(GetDomain.class).
		  getDomain(getModel().domain());

		return String.format(
		  "Лог тестирования домена %s '%s'",
		  d.getCode(), d.getName()
		);
	}

	public String[] getDefaultShuntGroups()
	{
		return new String[]
		{
		  "retrade:invoices",
		  "retrade:payment",
		  "retrade:aggr"
		};
	}

	public String[] getAllShuntGroups()
	{
		if(allShuntGroups != null)
			return allShuntGroups;

		//~: collect shunt groups
		Set<String> groups = SelfShuntPoint.getInstance().
		  collectShuntsGroups();

		return allShuntGroups =
		  groups.toArray(new String[groups.size()]);
	}

	private String[] allShuntGroups;

	public boolean isTestDomain()
	{
		return isTestPrimaryKey(getModel().getDomain());
	}

	public boolean isUpdating()
	{
		return getModel().isUpdating();
	}

	public void setUpdating(boolean v)
	{
		if(isTestDomain())
			getModel().setUpdating(v);
	}

	public boolean isGroupSelected(String name)
	{
		return getModel().getGroups().contains(name);
	}

	public String getSelfShuntLog()
	{
		return selfShuntLog;
	}

	private String selfShuntLog;


	/* protected: ModelView interface */

	protected SelfShuntGroupsModelBean createModel()
	{
		SelfShuntGroupsModelBean mb = new SelfShuntGroupsModelBean();

		//~: the domain to shunt
		mb.setDomain(obtainEntityKeyFromRequestStrict());
		if(bean(GetDomain.class).getDomain(mb.domain()) == null)
			throw new IllegalArgumentException();

		//~: check the default groups
		mb.getGroups().addAll(Arrays.asList(getDefaultShuntGroups()));

		//~: updating by default for test domains
		mb.setUpdating(isTestPrimaryKey(mb.getDomain()));

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof SelfShuntGroupsModelBean);
	}
}