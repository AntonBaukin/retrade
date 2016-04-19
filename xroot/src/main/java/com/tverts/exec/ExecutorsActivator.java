package com.tverts.exec;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: servlet (listeners) */

import com.tverts.servlet.listeners.ContextListenerBase;
import com.tverts.servlet.listeners.PickListener;


/**
 * Activates Root Executor of the ExecPoint.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickListener(order = 2000)
public class   ExecutorsActivator
       extends ContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	protected void init()
	{
		ExecPoint.getInstance().activate();
	}
}
