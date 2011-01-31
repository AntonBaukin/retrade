package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequestSingle;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;

public class   SeShRequestSingleHandler
       extends SeShInitialRequestsHandlerBase<SeShRequestSingle>
{
	/* protected: SeShInitialRequestsHandlerBase interface */

	protected Class<SeShRequestSingle>
	                  getRequestClass()
	{
		return SeShRequestSingle.class;
	}

	protected Set<String>
	                  selectShunts(SeShRequestSingle req)
	{
		return SelfShuntPoint.getInstance().
		  getShuntsSet().enumShuntsByName(
		    req.getSelfShuntKey().toString());
	}

	/* protected: logging */

	protected String logsig()
	{
		return "SeSh-RequestSingleHandler";
	}

	protected void   logSelectionEmpty(SeShRequestSingle req, Set<String> shunts)
	{
		if(shunts.isEmpty()) LU.W(getLog(), logsig(),
		  " has found NO shunts for the shunt unit name provided: [",
		  req.getSelfShuntKey(), "]");
	}

	protected void   logSelectionResults(SeShRequestSingle req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(),
		  " has found for the shunt unit name provided: [",
		  req.getSelfShuntKey(), "] the following shunts: [\n",
		  SU.a2s(shunts), "]");
	}
}