package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/* tverts.com: servlet */

import com.tverts.servlet.RequestPoint;

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
		if(event.getServletRequest() instanceof HttpServletRequest)
			RequestPoint.setRootRequest(
			  (HttpServletRequest)event.getServletRequest());
	}

	public void requestDestroyed(ServletRequestEvent event)
	{
		RequestPoint.setRootRequest(null);
	}
}