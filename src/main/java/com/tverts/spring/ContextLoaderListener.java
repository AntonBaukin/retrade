package com.tverts.spring;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps Spring Context Loader Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class   ContextLoaderListener
       extends ServletListenerWrapper
{
	protected Class getListenerClass()
	{
		return org.springframework.web.context.ContextLoaderListener.class;
	}
}
