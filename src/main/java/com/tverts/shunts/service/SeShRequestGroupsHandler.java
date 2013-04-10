package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequest;
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
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShRequestGroupsHandler
       extends SeShInitialRequestsHandlerBase
{
	/* protected: request handling */

	protected boolean     isKnownRequest(SeShRequest req)
	{
		return (req instanceof SeShRequestGroups);
	}

	protected Set<String> selectShunts(SeShRequest req)
	{
		return SelfShuntPoint.getInstance().getShuntsSet().
		  enumShuntsByGroups(((SeShRequestGroups)req).getGroups());
	}


	/* protected: logging */

	protected void   logSelectionEmpty(SeShRequest req)
	{
		LU.W(getLog(), logsig(), " has found NO shunts for the groups provided:",
		  " \n[", SU.a2s(((SeShRequestGroups)req).getGroups()), "]"
		);
	}

	protected void   logSelectionResults(SeShRequest req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(), " has found for the groups provided: [",
		  SU.a2s(((SeShRequestGroups)req).getGroups()),
		  "] the following shunts: \n[", SU.a2s(shunts), "]"
		);
	}
}