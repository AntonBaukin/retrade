package com.tverts.shunts.service;

/* com.tverts: shunts + shunt protocol */

import com.tverts.shunts.SelfShunt;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShRequestsSequence;


/**
 * Extends Self Shunts Executor to handle
 * {@link SeShRequestsSequence} requests.
 *
 * @author anton.baukin@gmail.com
 */
public class SeShRequestsSequenceHandler extends SeShExecutor
{
	/* protected: Shunt Unit execution */

	protected boolean     isKnownRequest(SeShRequest req)
	{
		return (req instanceof SeShRequestsSequence);
	}

	protected SeShRequest findNextRequest(SelfShunt shunt, SeShRequest request)
	{
		if(!(request instanceof SeShRequestsSequence))
			throw new IllegalArgumentException();

		//NOTE: that we clone the request, as the request
		//      instance may not be modified!

		SeShRequestsSequence req =
		  ((SeShRequestsSequence)request).clone();

		//?: {the sequence is not finished} has further request
		return (req.gotoNextShunt())?(req):(null);
	}
}