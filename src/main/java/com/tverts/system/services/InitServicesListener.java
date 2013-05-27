package com.tverts.system.services;

/* Spring Framework */

import com.tverts.system.tx.TxPoint;
import org.springframework.transaction.annotation.Transactional;

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