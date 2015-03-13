package com.tverts.system.services;

/* com.tverts: servlet (servlet) */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
public class   InitServicesListener
       extends ServletContextListenerBase
{
	/* Servlet Context Listener Base */

	protected void init()
	{
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				//!: init the services system
				ServicesPoint.system().init();
			}
		});
	}

	protected void destroy()
	{
		bean(TxBean.class).execute(new Runnable()
		{
			public void run()
			{
				//!: destroy the services system
				ServicesPoint.system().destroy();
			}
		});
	}
}