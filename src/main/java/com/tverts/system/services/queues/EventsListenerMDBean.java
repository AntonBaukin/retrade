package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;
import javax.jms.MessageListener;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system */

import com.tverts.system.SystemClassLoader;


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
		//~: bind system class loader
		SystemClassLoader.bind();

		try
		{
			//~: invoke the bean
			((EventsListenerBean)bean(EventsListenerBean.BEAN_NAME)).
			  takeEventMessage(msg);
		}
		finally
		{
			//~: unbind system class loader
			SystemClassLoader.unbind();
		}
	}
}