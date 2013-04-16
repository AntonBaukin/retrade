package com.tverts.spring;

/* Java Servlet api */

import javax.servlet.ServletContextListener;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ContextListenerWrapper;


/**
 * Wraps Spring Log4j Configuration Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class Log4jConfigListener extends ContextListenerWrapper
{
	protected Class<? extends ServletContextListener> getListenerClass()
	{
		return org.springframework.web.util.Log4jConfigListener.class;
	}
}