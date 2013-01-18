package com.tverts.exec.service;

/* com.tverts: api */

import com.tverts.api.Ping;
import com.tverts.api.Pong;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;
import com.tverts.exec.ExecPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.system.tx.TxPoint;


/**
 * Executor of {@link Ping} requests from the API.
 *
 * @author anton.baukin@gmail.com
 */
public class PingExecutor extends ExecutorBase
{
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
			pong.setObject(ExecPoint.execute(
			  ((Ping)ping).getRequest()
			));
		}
		catch(Throwable e)
		{
			pong.setError(EX.e2en(e));
			pong.setStack(EX.print(e));

			throw new ExecError(e).setResult(pong);
		}

		return pong;
	}
}