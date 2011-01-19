package com.tverts.shunts.service;

/* com.tverts: shunt protocol */

import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

/**
 * Request handler incapsulates the implementation
 * of actual invocation of shunt requests, including
 * the initial ones.
 *
 * Request handler must support at least one Java
 * class of requests.
 *
 * Request handler must be thread safe (fully
 * reentable).
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public boolean      canHandleRequest(SeShRequest request);

	public SeShResponse handleShuntRequest(SeShRequest request);
}