package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;

/* tverts.com: system */

import com.tverts.system.JTAPoint;


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
		//~: clean JTA bindings
		JTAPoint.jta().clean();

		if(event.getServletRequest() instanceof HttpServletRequest)
		{
			RequestPoint.setRootRequest(
			  (HttpServletRequest)event.getServletRequest());

			//~: generate the HTTP session
			((HttpServletRequest)event.getServletRequest()).getSession(true);
		}
	}

	public void requestDestroyed(ServletRequestEvent event)
	{
		RequestPoint.setRootRequest(null);

		//~: clean JTA bindings
		JTAPoint.jta().clean();
	}
}