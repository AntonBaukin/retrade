package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequestAll;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;

/**
 * Handles initial request to start all the
 * shunt units configured in the system's
 * {@link SelfShuntPoint}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SeShRequestAllHandler
       extends SeShInitialRequestsHandlerBase<SeShRequestAll>
{
	/* protected: SeShInitialRequestsHandlerBase interface */

	protected Class<SeShRequestAll>
	                 getRequestClass()
	{
		return SeShRequestAll.class;
	}

	protected Set<String>
	                 selectShunts(SeShRequestAll req)
	{
		return SelfShuntPoint.getInstance().
		  getShuntsSet().enumShunts();
	}

	/* protected: logging */

	protected String logsig()
	{
		return "SeSh-RequestAllHandler";
	}

	protected void   logSelectionEmpty(SeShRequestAll req, Set<String> shunts)
	{
		if(shunts.isEmpty()) LU.W(getLog(), logsig(),
		  " has found NO shunts registered in the system!");
	}

	protected void   logSelectionResults(SeShRequestAll req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(),
		  " has found for the following shunts in the system: [\n",
		  SU.a2s(shunts), "]");
	}
}