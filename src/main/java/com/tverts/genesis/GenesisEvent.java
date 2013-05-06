package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: system services (events) */

import com.tverts.system.services.events.EventBase;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * Service event to start Genesis on
 * the Spheres named.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisEvent extends EventBase
{
	public static final long serialVersionUID = 0L;


	/* public: GenesisEvent (bean) interface */

	/**
	 * Defines the Genesis Spheres to execute
	 * in the order of the list.
	 */
	public List<String> getSpheres()
	{
		return spheres;
	}

	public void setSpheres(List<String> spheres)
	{
		this.spheres = (spheres != null)?(spheres):
		  new ArrayList<String>(2);
	}

	/**
	 * Genesis parameters. (See Genesis log.)
	 */
	public Map<String, String> getParams()
	{
		return params;
	}

	public void setParams(Map<String, String> params)
	{
		this.params = (params != null)?(params):
		  new HashMap<String, String>(3);
	}

	/**
	 * The name of log parameter in 'Genesis' area
	 * to save log after the generation.
	 *
	 * By default is undefined, and no log is saved.
	 */
	public String getLogParam()
	{
		return logParam;
	}

	public void setLogParam(String logParam)
	{
		this.logParam = s2s(logParam);
	}


	/* private: parameters of the event */

	private List<String> spheres =
	  new ArrayList<String>(2);

	private String logParam;

	private Map<String, String> params =
	  new HashMap<String, String>(3);
}