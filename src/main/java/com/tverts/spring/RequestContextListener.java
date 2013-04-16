package com.tverts.spring;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps Spring Request Context Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class RequestContextListener extends ServletListenerWrapper
{
	protected Class getListenerClass()
	{
		return org.springframework.web.context.request.RequestContextListener.class;
	}
}