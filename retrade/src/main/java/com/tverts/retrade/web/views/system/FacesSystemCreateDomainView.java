package com.tverts.retrade.web.views.system;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

/* com.tverts: system (services) */

import com.tverts.system.services.ServicesPoint;

/* com.tverts: secure */

import static com.tverts.secure.SecPoint.loadSystemDomain;

/* com.tverts: genesis */

import com.tverts.genesis.GenesisEvent;
import com.tverts.genesis.GenesisPoint;

/* com.tverts: endure + retrade domain (core) */

import com.tverts.endure.core.Domain;
import com.tverts.endure.core.GetDomain;
import com.tverts.endure.core.GetProps;
import com.tverts.endure.core.Property;

/* com.tverts: retrade domain (core) */

import com.tverts.retrade.domain.core.CreateDomainModelBean;

/* com.tverts: objects */

import com.tverts.objects.ObjectParamView;

/* com.tverts: support */

import static com.tverts.support.DU.timefull2str;
import static com.tverts.support.SU.s2s;


/**
 * Faces view to create new Domain instance.
 *
 * @author anton.baukin@gmail.com
 */
@ManagedBean @RequestScoped
public class FacesSystemCreateDomainView extends ModelView
{
	/* actions */

	public String doCreateDomain()
	{
		if(getDomainCode() == null) throw new IllegalStateException();
		if(getDomainName() == null) throw new IllegalStateException();

		//?: {this code is used}
		if(isDomainExists())
			return null;

		//~: fill parameters map
		Map<String, String> params = new HashMap<String, String>();
		final String        P      = "createDomainParam_";

		//~: get parameters from the request
		for(String pn : request().getParameterMap().keySet())
			if(pn.startsWith(P))
			{
				String v = s2s(request().getParameter(pn));
				params.put(pn.substring(P.length()), v);
			}

		//~: code parameter
		params.put(getDomainCodeParam(), getDomainCode());

		//~: name parameter
		params.put(getDomainNameParam(), getDomainName());

		//~: is test flag
		if(Boolean.TRUE.equals(getModel().getTestDomain()))
			params.put(getTestDomainParam(), "true");

		//~: create genesis event
		GenesisEvent event = new GenesisEvent();

		//~: spheres
		event.getSpheres().addAll(
		  Arrays.asList(getGenesisSpheres()));

		//~: parameters
		event.setParams(params);

		//~: log parameter
		String log = "Log " + timefull2str(null);
		getModel().setLogParam(log);
		event.setLogParam(log);

		//!: send event for background execution
		ServicesPoint.send(
		  GenesisPoint.getInstance().getGenesisService().uid(),
		  event
		);

		return null;
	}

	public String gotoDoneCreateDomain()
	{
		return "done";
	}

	public String doGetGenesisLog()
	{
		//~: find the domain creating
		Domain domain = bean(GetDomain.class).
		  getDomain(getDomainCode());

		//?: {not found} get the system domain as error fallback
		if(domain == null)
			domain = loadSystemDomain();

		//~: load the log property
		GetProps get = bean(GetProps.class);
		Property log = get.get(domain, "Genesis", getModel().getLogParam());

		if(log != null)
			genesisLog = get.string(log);

		return null;
	}


	/* validation */

	public boolean isDomainCreateValid()
	{
		return !isDomainExists();
	}

	public boolean isDomainExists()
	{
		if(domainExists != null)
			return domainExists;

		if(getDomainCode() == null)
			return false;

		String code = s2s(getDomainCode());
		if(code == null) return false;

		return domainExists =
		  (bean(GetDomain.class).getDomain(code) != null);
	}

	private Boolean domainExists;


	/* public: FacesSystemCreateDomain (view) interface */

	public CreateDomainModelBean getModel()
	{
		return (CreateDomainModelBean) super.getModel();
	}

	public String[] getGenesisSpheres()
	{
		return getGenesisSpheres(
		  Boolean.TRUE.equals(getModel().getTestDomain()));
	}

	public String[] getGenesisSpheres(boolean test)
	{
		if(!test) return new String[] {
		  "Ordinary Domain"
		};

		return new String[] {
		  "Core Test",
		  "ReTrade Test Catalogs",
		  "ReTrade Test Documents"
		};
	}

	public String getDomainCodeParam()
	{
		for(ObjectParamView p : getModel().getParams())
			if(p.getName().endsWith("(DomainCode)"))
				return p.getName();

		throw new IllegalStateException(
		  "Domain Code genesis parameter is not found!");
	}

	public String getDomainNameParam()
	{
		for(ObjectParamView p : getModel().getParams())
			if(p.getName().endsWith("(DomainName)"))
				return p.getName();

		throw new IllegalStateException(
		  "Domain Name genesis parameter is not found!");
	}

	public String getTestDomainParam()
	{
		for(ObjectParamView p : getModel().getParams())
			if(p.getName().endsWith("(TestDomain)"))
				return p.getName();

		throw new IllegalStateException(
		  "Is Domain Test genesis parameter is not found!");
	}

	public String getWindowTitle()
	{
		if(Boolean.TRUE.equals(getModel().getTestDomain()))
			return "Создание тестового домена";
		return "Создание домена";
	}

	public List<String> getRestParams()
	{
		HashSet<String> names;

		if(restParams != null)
			return restParams;

		//~: get all the names
		ensureParamsMap();
		names = new HashSet<String>(params.keySet());

		//~: remove the code & name
		names.remove(getDomainCodeParam());
		names.remove(getDomainNameParam());
		names.remove(getTestDomainParam());

		//~: sort the result
		restParams = new ArrayList<String>(names);
		Collections.sort(restParams);

		return restParams;
	}

	private List<String> restParams;

	public String getGenesisLog()
	{
		return genesisLog;
	}

	private String genesisLog;


	/* public: FacesSystemCreateDomain (fields) interface */

	public String getParam(String name)
	{
		ensureParamsMap();
		if(!params.containsKey(name))
			throw new IllegalArgumentException();
		return params.get(name).getValue();
	}

	protected void ensureParamsMap()
	{
		if(params == null)
		{
			params = new HashMap<String, ObjectParamView>();
			for(ObjectParamView p : getModel().getParams())
				params.put(p.getName(), p);
		}
	}

	private Map<String, ObjectParamView> params;

	public void setParam(String name, String val)
	{
		ensureParamsMap();
		params.get(name).setValue(val);
	}

	public String getDomainCode()
	{
		return getParam(getDomainCodeParam());
	}

	public void setDomainCode(String domainCode)
	{
		setParam(getDomainCodeParam(), domainCode);
	}

	public String getDomainName()
	{
		return getParam(getDomainNameParam());
	}

	public void setDomainName(String domainName)
	{
		setParam(getDomainNameParam(), domainName);
	}


	/* protected: ModelView interface */

	protected CreateDomainModelBean createModel()
	{
		CreateDomainModelBean mb = new CreateDomainModelBean();

		//~: test domain
		boolean td = "true".equals(request().getParameter("testDomain"));
		mb.setTestDomain(td);

		//~: collect the parameters
		mb.setParams(GenesisPoint.getInstance().getGenesisService().
		  parameters(Arrays.asList(getGenesisSpheres(td))));

		return mb;
	}

	protected boolean isRequestModelMatch(ModelBean model)
	{
		return (model instanceof CreateDomainModelBean);
	}
}