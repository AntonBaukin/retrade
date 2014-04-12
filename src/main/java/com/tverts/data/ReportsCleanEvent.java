package com.tverts.data;

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.events.EventBase;

/**
 * Event that Reports Service sends to itself
 * to do obsolete reports cleanups.
 *
 * @author anton.baukin@gmail.com.
 */
public class      ReportsCleanEvent
       extends    EventBase
       implements DelayedEvent
{
	public static final long serialVersionUID = 0L;


	public long getEventTime()
	{
		return eventTime;
	}

	public void setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}

	public boolean isErase()
	{
		return erase;
	}

	public void setErase(boolean erase)
	{
		this.erase = erase;
	}


	/* private: event config */

	private long    eventTime;
	private boolean erase;
}