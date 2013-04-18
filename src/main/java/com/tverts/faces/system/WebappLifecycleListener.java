package com.tverts.faces.system;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps JSF 2.0 (RI) Web Application Lifecycle Listener.
 * This class is a Singletone.
 *
 * TODO Use filter instead of WebappLifecycleListener!
 *
 * Note that this class removes request handling methods,
 * and allows to call them in the filter.
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

	public void     contextInitialized(ServletContextEvent event)
	{
		//~: refer this as Singleton
		INSTANCE = this;

		super.contextInitialized(event);
	}


	/* protected: ServletListenerWrapper interface */

	protected Class getListenerClass()
	{
		return com.sun.faces.application.WebappLifecycleListener.class;
	}
}