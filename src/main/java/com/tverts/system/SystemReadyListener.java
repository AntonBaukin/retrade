package com.tverts.system;

/* com.tverts: webapp listeners */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: z-services */

import com.tverts.system.zservices.ServicesPoint;
import com.tverts.system.zservices.events.SystemReady;


/**
 * Invoked as the last listener of the application
 * initialization (startup) sequence.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   SystemReadyListener
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		//~: send services notification
		ServicesPoint.broadcast(new SystemReady());
	}
}