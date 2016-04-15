package com.tverts.system.services.queues;

/* Java Messaging */

import javax.jms.Message;


/**
 * Spring configured Bean doing actual processing
 * Z-Services Queue (Bus) Events and sending them
 * into the Services System.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface EventsListenerBean
{
	/**
	 * The name of the Spring configured Bean
	 * doing actual processing Z-Services Queue
	 * (Bus) Events.
	 */
	public static final String BEAN_NAME =
	  "servicesBusListener";


	/* Events Listener Bean */

	public void takeEventMessage(Message msg);
}