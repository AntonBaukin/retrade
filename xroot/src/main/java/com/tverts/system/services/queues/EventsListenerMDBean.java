package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.MessageListener;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system */

import com.tverts.system.JTAPoint;
import com.tverts.system.services.MainService;
import com.tverts.system.services.ServicesPoint;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Message-Driven Bean listening Z-Services
 * Events Queue (Bus). The events taken
 * from the queue are then sent to the
 * Services System for the execution.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class EventsListenerMDBean implements MessageListener
{
	/* public: MessageListener interface */

	public void onMessage(Message msg)
	{
		//?: {the system is not ready yet}
		if(!MainService.INSTANCE.isSystemReady())
		{
			//!: rollback the transaction
			try
			{
				JTAPoint.jta().tx().setRollbackOnly();
			}
			catch(Throwable e)
			{
				throw EX.wrap(e, "System is not ready to handle Queue messages, ",
				  "but transaction rollback was not possible!"
				);
			}

			try
			{
				String  id = msg.getStringProperty(JMSProtocol.SERVICE);
				boolean bc = msg.getBooleanProperty(JMSProtocol.BROADCAST);

				LU.W(ServicesPoint.LOG_SERVICE_BOOT, "message ",
				  (bc)?("broadcasted"):SU.cat("sent to [", id, "]"),
				  " was rolled back as the system is not ready yet!"
				);
			}
			catch(Throwable e)
			{
				throw EX.wrap(e);
			}

			return;
		}

		//~: invoke the bean
		((EventsListenerBean) bean(EventsListenerBean.BEAN_NAME)).
		  takeEventMessage(msg);
	}
}