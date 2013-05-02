package com.tverts.secure.system;

/* com.tverts: servlet (filters) */

import com.tverts.servlet.REQ;
import com.tverts.servlet.filters.FilterBase;
import com.tverts.servlet.filters.FilterStage;
import com.tverts.servlet.filters.FilterTask;


/**
 * This special filter is for localhost
 * requests. Place it at the end of the
 * filters pipe.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class LocalLoginFilter extends FilterBase
{
	/* public: FilterBase interface */

	public void openFilter(FilterTask task)
	{
		//?: {is not an external call}
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		//?: {this is not a localhost request} skip
		if(!REQ.isLocalhost(task.getRequest()))
			return;

		//~: explicitly forbid
		try
		{
			task.getResponse().sendError(403);
			task.setBreaked();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void closeFilter(FilterTask task)
	{}
}