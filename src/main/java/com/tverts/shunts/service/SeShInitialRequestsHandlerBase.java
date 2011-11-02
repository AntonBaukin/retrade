package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;

import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestInitial;
import com.tverts.shunts.protocol.SeShRequestsSequence;
import com.tverts.shunts.protocol.SeShResponse;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Base class for the initial request handlers.
 * Supports only one precious class of the request.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShInitialRequestsHandlerBase
                        <R extends SeShRequestInitial>
       implements     SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public boolean      canHandleRequest(SeShRequest request)
	{
		return (request instanceof SeShRequestInitial) &&
		  getRequestClass().getName().equals(
		    request.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public SeShResponse handleShuntRequest(SeShRequest request)
	{
		SeShBasicResponse res = new SeShBasicResponse(request);

		//~: select all registered shunt units
		Set<String>       sel = selectShunts((R)request);

		//?: {has al least one shunt unit} create the
		if(!sel.isEmpty())
			res.setNextRequest(new SeShRequestsSequence(
			  new ArrayList<String>(sel)));

		logSelection((R)request, sel);
		return res;
	}

	/* protected: request handling support */

	protected abstract Class<R>
	                 getRequestClass();

	protected abstract Set<String>
                    selectShunts(R req);

	/* protected: logging */

	protected String getLog()
	{
		return LU.getLogBased(SelfShuntPoint.LOG_SERVICE, this);
	}

	protected void   logSelection(R req, Set<String> shunts)
	{
		if(shunts.isEmpty() && !LU.isW(getLog()))
			return;

		//?: {found no shunts}
		if(shunts.isEmpty())
		{
			logSelectionEmpty(req, shunts);
			return;
		}

		if(!LU.isI(getLog()))
			return;

		logSelectionResults(req, shunts);
	}

	protected void   logSelectionEmpty(R req, Set<String> shunts)
	{}

	protected void   logSelectionResults(R req, Set<String> shunts)
	{}
}