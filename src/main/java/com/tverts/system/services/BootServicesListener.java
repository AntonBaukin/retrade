package com.tverts.system.services;

/* Java Servlet api */

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* Apache Log4J */

import org.apache.log4j.Logger;

public class      BootServicesListener
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent sce)
	{}

	public void contextDestroyed(ServletContextEvent sce)
	{}

}