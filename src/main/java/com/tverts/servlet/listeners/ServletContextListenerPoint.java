package com.tverts.servlet.listeners;

/**
 * A global point to register Servlet Context Listeners.
 *
 * @author anton.baukin@gmail.com
 */
public class   ServletContextListenerPoint
       extends ServletContextListenerBean
{
	/* Servlet Context Listener Point Singleton */

	public static final ServletContextListenerPoint INSTANCE =
	  new ServletContextListenerPoint();

	public static ServletContextListenerPoint getInstance()
	{
		return INSTANCE;
	}

	private ServletContextListenerPoint()
	{}
}