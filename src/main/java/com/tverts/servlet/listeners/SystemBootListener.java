package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* tverts.com: servlets */

import com.tverts.servlet.RequestPoint;

/**
 * Binds web application context with Servlet and
 * Java Server Faced support routines.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   SystemBootListener
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		RequestPoint.setContext(sce.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		RequestPoint.setContext(null);
	}
}