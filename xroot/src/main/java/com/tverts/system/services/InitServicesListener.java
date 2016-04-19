package com.tverts.system.services;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: transactions */

import com.tverts.system.tx.TxBean;

/* com.tverts: servlet (servlet) */

import com.tverts.servlet.listeners.ContextListenerBase;
import com.tverts.servlet.listeners.PickListener;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
@Component @PickListener(order = 2500)
public class   InitServicesListener
       extends ContextListenerBase
{
	/* Servlet Context Listener Base */

	protected void init()
	{
		//!: init the services system
		bean(TxBean.class).execute(() -> ServicesPoint.system().init());
	}

	protected void destroy()
	{
		//!: destroy the services system
		bean(TxBean.class).execute(() -> ServicesPoint.system().destroy());
	}
}