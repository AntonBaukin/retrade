package com.tverts.system.zservices.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.MessageListener;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


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
		((EventsListenerBean)bean(EventsListenerBean.BEAN_NAME)).
		  takeEventMessage(msg);
	}
}