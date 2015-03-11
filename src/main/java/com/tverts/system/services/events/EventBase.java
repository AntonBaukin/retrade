package com.tverts.system.services.events;

/* com.tverts: services */

import com.tverts.system.services.Event;


/**
 * Implementation base of an {@link Event}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EventBase implements Event
{
	public static final long serialVersionUID = 20140806L;


	/* Event */

	public String  getService()
	{
		return service;
	}

	private String service;

	public void    setService(String serviceUID)
	{
		this.service = serviceUID;
	}

	public Class   getEventType()
	{
		return eventType;
	}

	private Class  eventType;

	public void    setEventType(Class eventType)
	{
		this.eventType = eventType;
	}
}