package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequestGroups;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;

/**
 * Handles initial request to start the shunt units
 * configured in the system's {@link SelfShuntPoint}
 * and are in at least one of the groups stored in the
 * {@link SeShRequestGroups} request given.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShRequestGroupsHandler
       extends SeShInitialRequestsHandlerBase<SeShRequestGroups>
{
	/* protected: SeShInitialRequestsHandlerBase interface */

	protected Class<SeShRequestGroups>
	                  getRequestClass()
	{
		return SeShRequestGroups.class;
	}

	protected Set<String>
	                  selectShunts(SeShRequestGroups req)
	{
		return SelfShuntPoint.getInstance().
		  getShuntsSet().enumShuntsByGroups(req.getGroups());
	}

	/* protected: logging */

	protected String logsig()
	{
		return "SeSh-RequestGroupsHandler";
	}

	protected void   logSelectionEmpty(SeShRequestGroups req, Set<String> shunts)
	{
		if(shunts.isEmpty()) LU.W(getLog(), logsig(),
		  " has found NO shunts for the groups provided: \n[",
		  SU.a2s(req.getGroups()), "]");
	}

	protected void   logSelectionResults(SeShRequestGroups req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(), " has found for the groups provided: [",
		  SU.a2s(req.getGroups()), "] the following shunts: \n[",
		  SU.a2s(shunts), "]");
	}
}