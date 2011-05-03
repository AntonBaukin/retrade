package com.tverts.servlet.listeners;

/**
 * A global point to register Servlet Request Listeners.
 *
 * @author anton.baukin@gmail.com
 */
public class   ServletRequestListenerPoint
       extends ServletRequestListenerBean
{
	/* public: Singleton */

	public static ServletRequestListenerPoint getInstance()
	{
		return INSTANCE;
	}

	private static final ServletRequestListenerPoint INSTANCE =
	  new ServletRequestListenerPoint();

	protected ServletRequestListenerPoint()
	{}
}