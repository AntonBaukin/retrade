package com.tverts.system.services.events;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;

/* com.tverts: support */

import com.tverts.support.EX;


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


	/* Delayed Event */

	public long getEventTime()
	{
		return eventTime;
	}

	public void setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}

	public EventBase setEventDelay(long delay)
	{
		EX.assertx(delay >= 0L);
		this.eventTime = (delay == 0L)?(0L):
		  System.currentTimeMillis() + delay;
		return this;
	}


	/* private: event time */

	private long eventTime;
}