package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;

/* com.tverts: support */

import com.tverts.support.OU;
import com.tverts.support.SU;


/**
 * Event Run strategy that clones the Event
 * configured and sends (or broadcasts) it.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EventRunDelegate
       extends        EventRunBase
{
	/* public: EventRun interface */

	public boolean run(Event event)
	{
		//?: {it is not that event}
		if(!isThatEvent(event))
			return false;

		if(getEvent() != null)
		{
			//~: clone the event
			EventBase e = OU.cloneBest(getEvent());

			//?: {has no target service}
			if(getTargetService(event) == null)
				broadcast(e);
			else
				send(getTargetService(event), e);
		}

		return false;
	}

	/* public: EventRunDelegate interface */

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


	/* protected: delegating */

	protected abstract boolean isThatEvent(Event event);

	protected String           getTargetService(Event event)
	{
		return this.getService();
	}


	/* target service & the event prototype */

	private String    service;
	private EventBase event;
}