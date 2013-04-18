package com.tverts.spring;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps Spring Log4j Configuration Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class   Log4jConfigListener
       extends ServletListenerWrapper
{
	protected Class getListenerClass()
	{
		return org.springframework.web.util.Log4jConfigListener.class;
	}
}