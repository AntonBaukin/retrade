package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* tverts.com: servlets */

import com.tverts.servlet.RequestPoint;

/* tverts.com: system */

import com.tverts.system.SystemClassLoader;


/**
 * Prepares the system to work.
 *
 * Binds web application context with Servlet and
 * Java Server Faced support routines.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      SystemBootListener
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent event)
	{
		SystemClassLoader.init();
		RequestPoint.setContext(event.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent event)
	{
		RequestPoint.setContext(null);
	}
}