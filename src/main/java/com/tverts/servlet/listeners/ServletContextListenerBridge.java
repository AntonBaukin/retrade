package com.tverts.servlet.listeners;

/* Java Servlet */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Activate system components during the
 * web application startup. See
 * {@link ServletContextListenerBean}
 *
 * @author anton.baukin@gmail.com
 */
public class      ServletContextListenerBridge
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		ServletContextListenerPoint.INSTANCE.
		  contextInitialized(sce);
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
		ServletContextListenerPoint.INSTANCE.
		  contextDestroyed(sce);
	}
}