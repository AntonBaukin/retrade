package com.tverts.system.services;

/* Sprint Framework */

import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: transactions */

import com.tverts.system.tx.TxBean;

/* com.tverts: servlet (servlet) */

import com.tverts.servlet.listeners.PickListener;
import com.tverts.servlet.listeners.ServletContextListenerBase;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickListener(order = 2500)
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