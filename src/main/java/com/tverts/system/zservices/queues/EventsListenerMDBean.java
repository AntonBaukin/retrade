package com.tverts.system.zservices.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.MessageListener;

/* Enterprise Java Beans */

import javax.ejb.TransactionAttribute;

/* com.tverts: services */

import com.tverts.system.zservices.ServicesPoint;

/* com.tverts: support */

import com.tverts.support.LU;


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

	@TransactionAttribute
	public void onMessage(Message msg)
	{
		LU.I(getLog(), "got z-services event!");
	}


	/* protected: logging */

	protected String getLog()
	{
		return ServicesPoint.LOG_SERVICE_MAIN;
	}
}