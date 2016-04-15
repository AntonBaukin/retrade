package com.tverts.shunts.service;

/* standard Java classes */

import java.util.Set;

/* com.tverts: shunts + shunt protocol */

import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestAll;


/**
 * Handles initial request to start all the
 * shunt units configured in the system's
 * {@link SelfShuntPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public class   SeShRequestAllHandler
       extends SeShInitialRequestsHandlerBase
{
	/* protected: request handling */

	protected boolean     isKnownRequest(SeShRequest req)
	{
		return (req instanceof SeShRequestAll);
	}

	protected Set<String> selectShunts(SeShRequest req)
	{
		return SelfShuntPoint.getInstance().
		  getShuntsSet().enumShunts();
	}
}