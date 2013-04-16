package com.tverts.spring;

/* Java Servlet api */

import javax.servlet.ServletContextListener;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ContextListenerWrapper;


/**
 * Wraps Spring Context Loader Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class ContextLoaderListener extends ContextListenerWrapper
{
	protected Class<? extends ServletContextListener> getListenerClass()
	{
		return org.springframework.web.context.ContextLoaderListener.class;
	}
}
