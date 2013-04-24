package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;

/* tverts.com: system */

import com.tverts.system.JTAPoint;

/* tverts.com: model */

import com.tverts.model.ModelRequest;


/**
 * Handles each external access to the application.
 * Binds the request with Servlet related components.
 *
 * @author anton.baukin@gmail.com
 */
public class   WebApplicationRequestListener
       extends ServletRequestListenerBase
{
	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent event)
	{
		if(!(event.getServletRequest() instanceof HttpServletRequest))
			return;

		//?: {there are no requests in the stack}
		if(RequestPoint.requests() == 0)
		{
			//~: clean JTA bindings
			JTAPoint.jta().clean();

			//~: clear the model
			ModelRequest.getInstance().clear();
		}

		//!: put the request
		RequestPoint.getInstance().setRequest(
		  (HttpServletRequest)event.getServletRequest());
	}

	public void requestDestroyed(ServletRequestEvent event)
	{
		//!: pop the request
		RequestPoint.getInstance().setRequest(null);

		//?: {there are no requests in the stack}
		if(RequestPoint.requests() == 0)
		{
			//~: clean JTA bindings
			JTAPoint.jta().clean();

			//~: clear the model
			ModelRequest.getInstance().clear();
		}
	}
}