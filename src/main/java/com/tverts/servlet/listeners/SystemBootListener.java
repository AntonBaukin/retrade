package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;

/* tverts.com: servlets */

import com.tverts.servlet.RequestPoint;

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