package com.tverts.exec.service;

/* com.tverts: api */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.tverts.api.Payload;
import com.tverts.api.Ping;
import com.tverts.api.Pong;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;
import com.tverts.exec.ExecPoint;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Executor of {@link Ping} requests from the API.
 *
 * @author anton.baukin@gmail.com
 */
public class PingExecutor extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object ping)
	{
		if(!(ping instanceof Ping))
			return null;

		Pong pong = new Pong();

		//~: ping-pong key
		pong.setKey(((Ping)ping).getKey());

		if(((Ping)ping).getRequest() != null) try
		{
			//!: execute the request
			pong.setObject(
			  executeRequest(((Ping)ping).getRequest()));
		}
		catch(Throwable e)
		{
			pong.setError(EX.e2en(e));
			pong.setStack(EX.print(e));

			throw new ExecError(e).setResult(pong);
		}

		return pong;
	}


	/* protected: Ping execution */

	protected Object executeRequest(Object request)
	  throws Throwable
	{
		if(Payload.class.equals(request.getClass()))
			return executePayload((Payload)request);

		return ExecPoint.execute(request);
	}

	@SuppressWarnings("unchecked")
	protected Object executePayload(Payload payload)
	  throws Throwable
	{
		if(payload.getOperation() == null)
			return null;

		Object res = ExecPoint.execute(
		  payload.getOperation()
		);

		if(res == null)
			return null;

		if(res instanceof Collection)
			if(!(res instanceof List))
				res = new ArrayList((Collection)res);

		if(res instanceof Object[])
			res = Arrays.asList((Object[])res);

		if(res instanceof List)
			payload.setList((List)res);
		else
			payload.setObject(res);

		return payload;
	}
}