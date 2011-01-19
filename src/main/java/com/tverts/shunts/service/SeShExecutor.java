package com.tverts.shunts.service;

/* com.tverts: shunts, shunt protocol */

import com.tverts.shunts.SelfShunt;
import com.tverts.shunts.SelfShuntPoint;
import com.tverts.shunts.SelfShuntUnitReport;

import com.tverts.shunts.protocol.SeShBasicResponse;
import com.tverts.shunts.protocol.SeShRequest;
import com.tverts.shunts.protocol.SeShResponse;

/**
 * Base class providing implementation for actual
 * running the Shunt Unit referred from the request.
 *
 * It founds the Shunt Unit by it's unique key
 * stored in the request via asking the Shunt Point.
 *
 * This implementation is thread-safe (reentable).
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public abstract class SeShExecutor
       implements     SeShRequestsHandler
{
	/* public: SeShRequestsHandler interface */

	public SeShResponse    handleShuntRequest(SeShRequest request)
	{
		Object    shuntKey = request.getSelfShuntKey();

		//?: {the key is not defined}
		if(shuntKey == null)
			throw new IllegalArgumentException(
			  "Self Shunts Executor got request with the Shunt Unit's " +
			  "unique key undefined!");

		//?: {the key has unsupported format}
		if(!(shuntKey instanceof String))
			throw new IllegalArgumentException(String.format(
			  "Self Shunts Executor got request with the Shunt Unit's " +
			  "unique key of unsupported format (not a String), " +
			  "but of class '%s'!", shuntKey.getClass().getName()));

		SelfShunt shunt    = SelfShuntPoint.getInstance().
		  getShuntsSet().getShunt(shuntKey.toString());

		//?: {this shunt unit does not exist}
		if(shunt == null)
			throw new IllegalArgumentException(String.format(
			  "Self Shunts Executor got request with the Shunt Unit's " +
			  "unique key '%s' not mapped to actual unit instance!",
			  shuntKey.toString()));

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

	protected abstract SeShRequest  findNextRequest
	  (SelfShunt shunt, SeShRequest request);

	protected SeShResponse executeSelfShunt
	  (SelfShunt shunt, SeShRequest request)
	  throws Throwable
	{
		SeShResponse res = createResponse(shunt, request);

		//HINT: with correct implementation a Shunt Unit may not
		//      allow an exception to sneak out the run procedure.

		try
		{
			shunt.runShunt(res.getReport());
		}
		catch(Throwable e)
		{
			handleExecError(shunt, res, e);
		}

		return res;
	}

	/**
	 * Handles unexpected execution error, not that is detected
	 * in the shunt unit's procedures itself (assertions).
	 *
	 * This implementation allows to continue shunting reraising
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
		SeShBasicResponse res = new SeShBasicResponse(request);

		res.setNextRequest(findNextRequest(shunt, request));
		res.setReport(new SelfShuntUnitReport());
		return res;
	}

	protected SeShResponse           createErrorResponse
	   (SelfShunt shunt, SeShRequest request, Throwable error)
	{
		SeShBasicResponse res = new SeShBasicResponse(request);

		res.setSystemError(error);
		return res;
	}
}