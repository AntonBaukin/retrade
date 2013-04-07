package com.tverts.system.services;

/* com.tverts: webapp listeners */

import com.tverts.servlet.listeners.ServletContextListenerBase;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
public class   InitServicesListener
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		ServicesPoint.system().init();
	}
}