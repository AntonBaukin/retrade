package com.tverts.shunts.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Set;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShuntPoint;

import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestAll;
import com.tverts.shunts.protocol.SeShRequestsSequence;
import com.tverts.shunts.protocol.SeShResponse;

/**
 * Handles initial request to start all the
 * shunt units configured in the system's
 * {@link SelfShuntPoint}.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class      SeShRequestAllHandler
       implements SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public boolean      canHandleRequest(SeShRequest request)
	{
		return SeShRequestAll.class.getName().equals(
		  request.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public SeShResponse handleShuntRequest(SeShRequest request)
	{
		SeShBasicResponse res = new SeShBasicResponse(request);

		//~: select all registered shunt units
		Set<String>       all = SelfShuntPoint.getInstance().
		  getShuntsSet().enumShunts();

		//?: {has al least one shunt unit} create the
		if(!all.isEmpty())
			res.setNextRequest(new SeShRequestsSequence(
			  new ArrayList<String>(all)));

		return res;
	}
}