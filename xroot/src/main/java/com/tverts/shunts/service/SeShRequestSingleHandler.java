package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestSingle;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Executes {@link SeShRequestSingle} requests.
 *
 * Note that despite the type of the request, 'single',
 * it may invoke more than one Shunt Units. That is
 * possible as the names of the units must not be unique:
 * their unique ids are created in {@link SelfShuntsSet}s.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShRequestSingleHandler
       extends SeShInitialRequestsHandlerBase
{
	/* protected: request handling */

	protected boolean     isKnownRequest(SeShRequest req)
	{
		return (req instanceof SeShRequestSingle);
	}

	protected Set<String> selectShunts(SeShRequest req)
	{
		return SelfShuntPoint.getInstance().getShuntsSet().
		  enumShuntsByName(req.getSelfShuntKey().toString());
	}


	/* protected: logging */

	protected void   logSelectionEmpty(SeShRequest req)
	{
		LU.W(getLog(), logsig(),
		  " had found no shunts for the name provided: [",
		  req.getSelfShuntKey(), "]"
		);
	}

	protected void   logSelectionResults(SeShRequest req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(), " had found for the name provided: [",
		  ((SeShRequestSingle)req).getSelfShuntKey(),
		  "] the following shunts: \n[", SU.a2s(shunts), "]"
		);
	}
}