package com.tverts.genesis;

/* com.tverts: system services */

import com.tverts.system.services.events.ServiceEventBase;


/**
 * {@link GenesisService} sends this event to Main Service
 * after the generation was breaked due an error.
 *
 * @author anton.baukin@gmail.com
 */
public class GenesisFailed extends ServiceEventBase
{
	public static final long serialVersionUID = 0L;


	/* public: GenesisDone (bean) interface */

	public GenesisEvent getEvent()
	{
		return event;
	}

	public void setEvent(GenesisEvent event)
	{
		this.event = event;
	}

	public Throwable getError()
	{
		return error;
	}

	public void setError(Throwable error)
	{
		this.error = error;
	}


	/* the original genesis event */

	private GenesisEvent event;
	private Throwable    error;
}