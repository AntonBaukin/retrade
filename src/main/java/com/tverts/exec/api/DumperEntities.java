package com.tverts.exec.api;

/* com.tverts: api */

import com.tverts.api.core.DumpEntities;

/* com.tverts: execution */

import com.tverts.exec.ExecutorBase;


/**
 * Executor of {@link DumpEntities} requests.
 *
 * @author anton.baukin@gmail.com
 */
public class DumperEntities extends ExecutorBase
{
	/* public: Executor interface */

	public Object execute(Object object)
	{
		if(!(object instanceof DumpEntities))
			return null;


		return null;
	}
}