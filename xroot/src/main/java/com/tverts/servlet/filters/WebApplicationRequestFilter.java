package com.tverts.servlet.filters;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;

/* tverts.com: system */

import com.tverts.system.JTAPoint;


/**
 * Binds the response with Servlet related components.
 * Related to {@code WebApplicationRequestListener}.
 * The first Filter invoked.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickFilter(order = { 0 })
public class WebApplicationRequestFilter extends FilterBase
{
	/* public: Filter interface */

	public void openFilter(FilterTask task)
	{
		//?: {there are no requests in the stack}
		if(RequestPoint.requests() == 0)
			initRequest();

		RequestPoint.getInstance().setRequest(task.getRequest());
		RequestPoint.getInstance().setResponse(task.getResponse());
	}

	public void closeFilter(FilterTask task)
	{
		RequestPoint.getInstance().setResponse(null);
		RequestPoint.getInstance().setRequest(null);

		//?: {there are no requests in the stack}
		if(RequestPoint.requests() == 0)
			freeRequest();
	}


	/* protected: request initialization */

	protected void initRequest()
	{
		freeRequest();
	}

	protected void freeRequest()
	{
		//~: clean JTA bindings
		JTAPoint.jta().clean();
	}
}