package com.tverts.jsx;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.events.EventBase;


/**
 * Event that notifies {@link JsX} service
 * to act invalidation of cached scripts.
 *
 * @author anton.baukin@gmail.com
 */
public class      JsCheckEvent
       extends    EventBase
       implements DelayedEvent
{
	public static final long serialVersionUID = 20151013L;


	/* Delayed Event */

	public long getEventTime()
	{
		return eventTime;
	}

	private long eventTime;

	public void setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}
}
