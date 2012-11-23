package com.tverts.servlet.listeners;

/* tverts.com: servlets */

import com.tverts.servlet.RequestPoint;


/**
 * Binds web application context with Servlet and
 * Java Server Faced support routines.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SystemBootListener
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		RequestPoint.setContext(getEvent().getServletContext());
	}

	protected void destroy()
	{
		RequestPoint.setContext(null);
	}
}