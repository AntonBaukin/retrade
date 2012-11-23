package com.tverts.exec;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.ServletContextListenerBase;


/**
 * Activates Root Executor of the ExecPoint.
 *
 * @author anton.baukin@gmail.com
 */
public class   ExecutorsActivator
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		ExecPoint.getInstance().activate();
	}
}
