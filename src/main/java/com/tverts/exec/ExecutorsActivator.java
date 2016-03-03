package com.tverts.exec;

/* Sprint Framework */

import org.springframework.stereotype.Component;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.PickListener;
import com.tverts.servlet.listeners.ServletContextListenerBase;


/**
 * Activates Root Executor of the ExecPoint.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickListener(order = 2000)
public class   ExecutorsActivator
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		ExecPoint.getInstance().activate();
	}
}
