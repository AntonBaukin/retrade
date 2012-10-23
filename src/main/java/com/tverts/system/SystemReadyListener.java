package com.tverts.system;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* com.tverts: webapp listeners */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: z-services */

import com.tverts.system.zservices.ServicesPoint;
import com.tverts.system.zservices.events.SystemReady;


/**
 * Invoked as the last listener of the application
 * initialization (startup) sequence.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SystemReadyListener
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		//~: send services notification
		ServicesPoint.broadcast(new SystemReady());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}
}