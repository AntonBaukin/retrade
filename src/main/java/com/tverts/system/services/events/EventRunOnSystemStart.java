package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;

/* com.tverts: support */

import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * {@link EventRun} strategy that clones and sends
 * the {@link Event} configured on receiving
 * {@link SystemReady} event.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class EventRunOnSystemStart extends EventRunBase
{
	/* public: EventRun interface */

	public boolean run(Event event)
	{
		//?: {it is not a System Ready}
		if(!(event instanceof SystemReady))
			return false;

		if(getEvent() != null)
		{
			//~: clone the event
			EventBase e = OU.cloneBest(getEvent());

			//?: {has no service}
			if(getService() == null)
				broadcast(e);
			else
				send(getService(), e);
		}

		return false;
	}


	/* public: EventAsRun interface */

	/**
	 * Target service. If not specified,
	 * the event is broadcasted.
	 */
	public String    getService()
	{
		return service;
	}

	public void      setService(String service)
	{
		this.service = SU.s2s(service);
	}

	public EventBase getEvent()
	{
		return event;
	}

	public void      setEvent(EventBase event)
	{
		this.event = event;
	}


	/* target service & the event prototype */

	private String    service;
	private EventBase event;
}