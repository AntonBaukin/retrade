package com.tverts.shunts.service;

/* standard Java classes */

import java.util.HashMap;
import java.util.Map;

/* com.tverts: system services (events) */

import com.tverts.system.services.events.EventBase;


/**
 * Basic class for Self-Shunt Events that
 * are accepted by {@link SelfShuntService}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SelfShuntEvent
       extends        EventBase
{
	public static final long serialVersionUID = 0L;


	/* public: SelfShuntEvent (bean) interface */

	public Long getDomain()
	{
		return domain;
	}

	public void setDomain(Long domain)
	{
		this.domain = domain;
	}

	public boolean isReadonly()
	{
		return readonly;
	}

	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}

	public Map<String, String> getParams()
	{
		return params;
	}

	public void setParams(Map<String, String> params)
	{
		if(params == null)
			params = new HashMap<String, String>(1);
		this.params = params;
	}


	/* private: the event parameters */

	private Long                domain;
	private boolean             readonly;

	private Map<String, String> params =
	  new HashMap<String, String>(1);
}
