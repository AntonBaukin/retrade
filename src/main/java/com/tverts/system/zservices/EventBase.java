package com.tverts.system.zservices;

/**
 * Implementation base of an {@link Event}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class EventBase implements Event
{
	public static final long serialVersionUID = 0L;


	/* public: Event interface */

	public String getService()
	{
		return serviceUID;
	}

	public void   setService(String serviceUID)
	{
		this.serviceUID = serviceUID;
	}

	public Class  getEventType()
	{
		return eventType;
	}

	public void   setEventType(Class eventType)
	{
		this.eventType = eventType;
	}


	/* event attributes */

	private String serviceUID;
	private Class  eventType;
}