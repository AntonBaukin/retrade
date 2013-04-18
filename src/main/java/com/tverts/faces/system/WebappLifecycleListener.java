package com.tverts.faces.system;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps JSF 2.0 Web Application Lifecycle Listener.
 *
 * Disables request listening to move this
 * issue to the special filter.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   WebappLifecycleListener
       extends ServletListenerWrapper
{
	/**
	 * Single instance initialized on
	 * the servlet container (server) start.
	 */
	public static volatile WebappLifecycleListener INSTANCE;


	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent event)
	{
		INSTANCE = this;
		super.contextInitialized(event);
	}


	/* public: ServletRequestListener interface */

	public void requestInitialized(ServletRequestEvent event)
	{}

	public void requestDestroyed(ServletRequestEvent event)
	{}

	public void requestInitializeCall(ServletRequestEvent event)
	{
		super.requestInitialized(event);
	}

	public void requestDestroyCall(ServletRequestEvent event)
	{
		super.requestDestroyed(event);
	}


	/* protected: ServletListenerWrapper interface */

	protected Class getListenerClass()
	{
		return com.sun.faces.application.WebappLifecycleListener.class;
	}
}