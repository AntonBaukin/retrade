package com.tverts.system.zservices.events;

/**
 * Extends {@link EventBase} basic implementation
 * to meet {@link ServiceEvent} interface.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      ServiceEventBase
       extends    EventBase
       implements ServiceEvent
{
	public static final long serialVersionUID = 0L;


	/* public: ServiceEventBase interface */

	public String getSourceService()
	{
		return sourceService;
	}

	public void   setSourceService(String suid)
	{
		this.sourceService = suid;
	}


	/* source service uid */

	private String sourceService;
}