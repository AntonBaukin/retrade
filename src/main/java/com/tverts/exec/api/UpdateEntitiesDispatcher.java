package com.tverts.exec.api;

/* com.tverts: execution */

import com.tverts.exec.ExecError;
import com.tverts.exec.ExecPoint;
import com.tverts.exec.ExecutorBase;

/* com.tverts: api */

import com.tverts.api.core.Holder;
import com.tverts.api.core.UpdateEntities;


/**
 * Dispatches API request {@link UpdateEntities}
 * into updating the entities list given.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class UpdateEntitiesDispatcher extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object request)
	  throws ExecError
	{
		if(!(request instanceof UpdateEntities))
			return null;

		//c: execute each holder of the update list
		for(Holder h : ((UpdateEntities)request).getEntities())
			ExecPoint.execute(new UpdateHolder(h));

		return Boolean.TRUE;
	}
}