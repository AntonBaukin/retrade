package com.tverts.genesis;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: system services (events) */

import com.tverts.system.services.events.EventBase;


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


	/* private: parameters of the event */

	private List<String> spheres =
	  new ArrayList<String>(2);

	private Map<String, String> params =
	  new HashMap<String, String>(3);
}