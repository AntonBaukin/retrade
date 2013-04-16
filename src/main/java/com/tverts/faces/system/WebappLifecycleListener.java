package com.tverts.faces.system;

/* com.tverts: servlets */

import com.tverts.servlet.listeners.ServletListenerWrapper;


/**
 * Wraps JSF 2.0 (RI) Web Application Lifecycle Listener.
 *
 * @author anton.baukin@gmail.com
 */
public class WebappLifecycleListener extends ServletListenerWrapper
{
	protected Class getListenerClass()
	{
		return com.sun.faces.application.WebappLifecycleListener.class;
	}
}