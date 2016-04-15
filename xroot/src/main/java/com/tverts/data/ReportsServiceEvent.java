package com.tverts.data;

/* com.tverts.system: services */

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.events.EventBase;


/**
 * Event that Reports Service sends to itself
 * to do obsolete reports cleanups.
 *
 * @author anton.baukin@gmail.com.
 */
public class      ReportsServiceEvent
       extends    EventBase
       implements DelayedEvent
{
	public static final long serialVersionUID = 20150316L;


	/* Reports Service Event */

	public long getEventTime()
	{
		return eventTime;
	}

	private long eventTime;

	public void setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}

	public boolean isStartup()
	{
		return startup;
	}

	private boolean startup;

	public void setStartup(boolean startup)
	{
		this.startup = startup;
	}

	public boolean isErase()
	{
		return erase;
	}

	private boolean erase;

	public void setErase(boolean erase)
	{
		this.erase = erase;
	}
}