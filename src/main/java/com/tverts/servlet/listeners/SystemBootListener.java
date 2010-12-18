package com.tverts.servlet.listeners;

/* Java Servlet api */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* tverts.com: servlets */

import com.tverts.servlet.RequestPoint;

public class      SystemBootListener
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{
		RequestPoint.setContext(sce.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{}
}