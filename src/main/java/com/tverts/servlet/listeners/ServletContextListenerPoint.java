package com.tverts.servlet.listeners;

/**
 * A global point to register Servlet Context Listeners.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class   ServletContextListenerPoint
       extends ServletContextListenerBean
{
	/* public: Singleton */

	public static ServletContextListenerPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ServletContextListenerPoint INSTANCE =
	  new ServletContextListenerPoint();

	protected ServletContextListenerPoint()
	{}
}