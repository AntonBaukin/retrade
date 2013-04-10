package com.tverts.shunts.service;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShunt;
import com.tverts.shunts.SelfShuntCtx;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntUnitReport;

import com.tverts.shunts.protocol.SeShResponseBase;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

/* com.tverts: support */

import static com.tverts.support.OU.cloneStrict;


/**
 * Base class providing implementation for actual
 * running the Shunt Unit referred from the request.
 *
 * It founds the Shunt Unit by it's unique key
 * stored in the request via asking the Shunt Point.
 *
 * This implementation is thread-safe (reentable).
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class SeShExecutor
       implements     SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public SeShResponse handleShuntRequest(SeShRequest request)
	{
		//?: {this request is not known}
		if(!isKnownRequest(request))
			return null;

		Object shuntKey = request.getSelfShuntKey();

		//?: {the key is not defined}
		if(shuntKey == null) throw new IllegalArgumentException(
		  "Self Shunts Executor got request with the Shunt Unit's " +
		  "unique key undefined!");

		//?: {the key has unsupported format}
		if(!(shuntKey instanceof String))
			throw new IllegalArgumentException(String.format(
			  "Self Shunts Executor got request with the Shunt Unit's " +
			  "unique key of unsupported format (not a String), " +
			  "but of class '%s'!", shuntKey.getClass().getName()));

		//~: get the copy of the shunt
		SelfShunt shunt = obtainShunt(shuntKey.toString());

		//?: {this shunt unit does not exist}
		if(shunt == null) throw new IllegalArgumentException(String.format(
		  "Self Shunts Executor got request with the Shunt Unit's " +
		  "unique key '%s' not mapped to actual unit instance!", shuntKey.toString()
		));

		//!: execute the shunt
		try
		{
			return executeSelfShunt(shunt, request);
		}
		catch(Throwable e)
		{
			return createErrorResponse(shunt, request, e);
		}
	}


	/* protected: Shunt Unit execution */

	protected abstract boolean      isKnownRequest(SeShRequest req);

	protected abstract SeShRequest  findNextRequest
	  (SelfShunt shunt, SeShRequest request);

	protected SeShResponse          executeSelfShunt
	  (SelfShunt shunt, SeShRequest request)
	  throws Throwable
	{
		SeShResponse res = createResponse(shunt, request);

		//HINT: with correct implementation a Shunt Unit may not
		//      allow an exception to sneak out the run procedure.

		try
		{
			SelfShuntCtx ctx = new SelfShuntCtx(
			  null, res.getReport(), null, false
			);

			shunt.shunt(ctx);
		}
		catch(Throwable e)
		{
			handleExecError(shunt, res, e);
		}

		return res;
	}

	protected SelfShunt             obtainShunt(String shuntKey)
	{
		SelfShunt shunt = SelfShuntPoint.getInstance().
		  getShuntsSet().getShunt(shuntKey);

		return (shunt == null)?(null):(cloneShunt(shunt));
	}

	protected SelfShunt             cloneShunt(SelfShunt shunt)
	{
		return cloneStrict(shunt);
	}

	/**
	 * Handles unexpected execution error, not that is detected
	 * in the shunt unit's procedures itself (assertions).
	 *
	 * This implementation allows to continue shunting re-raising
	 * the exception given.
	 */
	protected SeShResponse          handleExecError
	   (SelfShunt shunt, SeShResponse response, Throwable error)
	  throws Throwable
	{
		throw error;
	}

	/**
	 * Creates a response having this and next requests
	 * initialized, and the report instance is set.
	 */
	protected SeShResponse          createResponse
	   (SelfShunt shunt, SeShRequest request )
	{
		SeShResponseBase res = new SeShResponseBase(request);

		//~: find the next request
		res.setNextRequest(findNextRequest(shunt, request));

		//~: create empty report
		res.setReport(new SelfShuntUnitReport());
		return res;
	}

	protected SeShResponse          createErrorResponse
	   (SelfShunt shunt, SeShRequest request, Throwable error)
	{
		SeShResponseBase res = new SeShResponseBase(request);

		res.setSystemError(error);
		return res;
	}
}