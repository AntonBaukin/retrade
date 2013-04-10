package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;

import com.tverts.shunts.protocol.SeShResponseBase;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestsSequence;
import com.tverts.shunts.protocol.SeShResponse;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Base class for the initial request handlers.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShInitialRequestsHandlerBase
       implements     SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	@SuppressWarnings("unchecked")
	public SeShResponse handleShuntRequest(SeShRequest req)
	{
		//?: {this request is not known}
		if(!isKnownRequest(req))
			return null;

		//~: select all registered shunt units
		Set<String>  sel = selectShunts(req);

		//~: create the response
		SeShResponse res = createResponse(req, sel);

		//~: log the selection
		logSelection(req, sel);

		return res;
	}


	/* protected: request handling */

	protected abstract boolean     isKnownRequest(SeShRequest req);

	protected abstract Set<String> selectShunts(SeShRequest req);

	protected SeShResponse         createResponse(SeShRequest req, Set<String> sel)
	{
		//~: create the response base
		SeShResponseBase res = new SeShResponseBase(req);

		//?: {has al least one shunt unit} create the
		if(!sel.isEmpty())
			res.setNextRequest(new SeShRequestsSequence(
			  new ArrayList<String>(sel)));

		return res;
	}


	/* protected: logging */

	protected String getLog()
	{
		return LU.getLogBased(SelfShuntPoint.LOG_SERVICE, this);
	}

	protected String logsig()
	{
		return getClass().getSimpleName();
	}

	protected void   logSelection(SeShRequest req, Set<String> shunts)
	{
		//?: {found no shunts}
		if(shunts.isEmpty())
		{
			logSelectionEmpty(req);
			return;
		}

		if(!LU.isI(getLog()))
			return;

		logSelectionResults(req, shunts);
	}

	protected void   logSelectionEmpty(SeShRequest req)
	{
		LU.W(getLog(), logsig(), " had found no shunts registered in the system!");
	}

	protected void   logSelectionResults(SeShRequest req, Set<String> shunts)
	{
		LU.W(getLog(), logsig(), " had found for the following shunts in the system:",
		  " \n[", SU.a2s(shunts), "]"
		);
	}
}