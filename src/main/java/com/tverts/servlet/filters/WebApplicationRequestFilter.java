package com.tverts.servlet.filters;

/* Sprint Framework */

import org.springframework.stereotype.Component;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;


/**
 * Binds the response with Servlet related components.
 * Related to {@code WebApplicationRequestListener}.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 0 })
public class WebApplicationRequestFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		RequestPoint.getInstance().setRequest(task.getRequest());
		RequestPoint.getInstance().setResponse(task.getResponse());
	}

	public void closeFilter(FilterTask task)
	{
		RequestPoint.getInstance().setResponse(null);
		RequestPoint.getInstance().setRequest(null);
	}
}