package com.tverts.system.services;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: servlet (webapp listeners) */

import com.tverts.servlet.listeners.ServletContextListenerBase;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.TxPoint;


/**
 * Initializes Z-Services System in the Point.
 *
 * @author anton.baukin@gmail.com
 */
public class   InitServicesListener
       extends ServletContextListenerBase
{
	/* protected: ServletContextListenerBase interface */

	@Transactional(rollbackFor = Throwable.class)
	protected void init()
	{
		TxPoint.getInstance().setTxContext();

		try
		{
			//!: init the services system
			ServicesPoint.system().init();
		}
		finally
		{
			TxPoint.getInstance().setTxContext(null);
		}
	}
}