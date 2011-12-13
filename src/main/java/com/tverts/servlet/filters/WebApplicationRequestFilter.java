package com.tverts.servlet.filters;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;

/* tverts.com: model */

import com.tverts.model.ModelRequest;


/**
 * Binds the response with Servlet related components.
 * Related to {@code WebApplicationRequestListener}.
 *
 * @author anton.baukin@gmail.com
 */
public class WebApplicationRequestFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		RequestPoint.setResponse(task.getResponse());
	}

	public void closeFilter(FilterTask task)
	{
		if(!FilterStage.REQUEST.equals(task.getFilterStage()))
			return;

		RequestPoint.setResponse(null);
		ModelRequest.getInstance().clear();
	}
}