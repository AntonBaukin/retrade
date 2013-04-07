package com.tverts.system.services.events;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;


/**
 * Service event with possible delay effect.
 *
 * @author anton.baukin@gmail.com
 */
public class      ServiceDelayedEventBase
       extends    ServiceEventBase
       implements DelayedEvent
{
	public static final long serialVersionUID = 0L;


	/* public: DelayedEvent interface */

	public long getEventTime()
	{
		return eventTime;
	}

	public void setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}

	public void setEventDelay(long delay)
	{
		if(delay < 0L) throw new IllegalArgumentException();
		this.eventTime = (delay == 0L)?(0L):
		  System.currentTimeMillis() + delay;
	}


	/* private: event time */

	private long eventTime;
}