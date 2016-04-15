package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: shunt protocol */

import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;


/**
 * Delegates processing the income requests
 * to the dispatchers listed in the configuration.
 *
 * @author anton.baukin@gmail.com
 */
public class      SeShRequestsDispatcher
       implements SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public SeShResponse handleShuntRequest(SeShRequest request)
	{
		//c: for all the handlers configured
		for(SeShRequestsHandler handler : handlers)
		{
			//~: try execute the handler
			SeShResponse res = handler.handleShuntRequest(request);

			//?: {has the response}
			if(res != null)
				return res;
		}

		//!: not found how to process the request
		throw new IllegalStateException(String.format(
		  "Self Shunts Request Dispatcher can't find handler for the " +
		  "request of class '%s'!", request.getClass().getName())
		);
	}


	/* public: SeShRequestsDispatcher (config) interface */

	public void setHandlers(List<SeShRequestsHandler> handlers)
	{
		if(handlers == null)
			handlers = new ArrayList<SeShRequestsHandler>(0);
		this.handlers = new ArrayList<SeShRequestsHandler>(handlers);
	}


	/* private: the handlers list */

	private List<SeShRequestsHandler> handlers =
	  new ArrayList<SeShRequestsHandler>(0);
}