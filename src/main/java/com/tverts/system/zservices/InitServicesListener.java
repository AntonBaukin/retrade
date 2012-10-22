package com.tverts.system.zservices;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* com.tverts: webapp listeners */

import com.tverts.servlet.listeners.ServletContextListenerBase;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
public class   InitServicesListener
       extends ServletContextListenerBase
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		ServicesPoint.system().init();
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}
}