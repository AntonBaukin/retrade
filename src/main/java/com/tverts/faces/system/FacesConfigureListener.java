package com.tverts.faces.system;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;

/* JavaServer Faces */

import com.sun.faces.config.ConfigureListener;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Starts Faces configuration.
 *
 * Disables request listening to move this
 * issue to the special filter.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   FacesConfigureListener
       extends ServletListenerWrapper
{
	public static volatile FacesConfigureListener INSTANCE;


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
		return ConfigureListener.class;
	}
}