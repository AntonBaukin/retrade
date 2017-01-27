package com.tverts.model.store;

/* com.tverts: services */

import com.tverts.system.services.DelayedEvent;
import com.tverts.system.services.events.EventBase;


/**
 * Internal event of Models Store Service.
 *
 * @author anton.baukin@gmail.com
 */
public class      ModelsStoreEvent
       extends    EventBase
       implements DelayedEvent
{
	public static final long serialVersionUID = 20150311L;


	/* Models Store Event */

	public long     getEventTime()
	{
		return eventTime;
	}

	private long    eventTime;

	public void     setEventTime(long eventTime)
	{
		this.eventTime = eventTime;
	}

	/**
	 * This flag tells the service
	 * to cleanup the persistent backend.
	 */
	public boolean  isSweep()
	{
		return sweep;
	}

	private boolean sweep;

	public void     setSweep(boolean sweep)
	{
		this.sweep = sweep;
	}

	/**
	 * This flag tells the service to save Model
	 * Beans removed from the in-memory cache.
	 */
	public boolean  isSynch()
	{
		return synch;
	}

	private boolean synch;

	public void     setSynch(boolean synch)
	{
		this.synch = synch;
	}
}