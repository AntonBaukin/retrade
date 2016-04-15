package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;


/**
 * Event to Self-Shunt Service to create
 * and run Shunts of the groups listed.
 *
 * Note that the order of groups doesn't
 * define the order of the shunts.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SelfShuntGroupsEvent
       extends SelfShuntEvent
{
	public static final long serialVersionUID = 0L;


	/* public: SelfShuntGroupsEvent (bean) interface */

	public List<String> getGroups()
	{
		return groups;
	}

	public void setGroups(List<String> groups)
	{
		if(groups == null)
			groups = new ArrayList<String>(2);
		this.groups = groups;
	}


	/* private: parameters of the event */

	private List<String> groups =
	  new ArrayList<String>(2);
}