package com.tverts.servlet.listeners;

/* Java Servlet */

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Activates system components during
 * the web application startup.
 *
 * @author anton.baukin@gmail.com
 */
public class      ContextListenerBridge
       implements ServletContextListener
{
	/* public: ServletContextListener interface */

	public void contextInitialized(ServletContextEvent event)
	{
		ServletContextListener[] ls =
		  bean(ContextListenersPoint.class).getListeners();

		for(ServletContextListener l : ls)
			l.contextInitialized(event);
	}

	public void contextDestroyed(ServletContextEvent event)
	{
		ServletContextListener[] ls =
		  bean(ContextListenersPoint.class).getListeners();

		//~: invoke listeners in reversed order
		for(int i = ls.length - 1;(i >= 0);i--) try
		{
			ls[i].contextDestroyed(event);
		}
		catch(Throwable e)
		{
			LU.E(LU.cls(this), e);
		}
	}
}