package com.tverts.system.zservices;

/**
 * Strategy of sending the requests for
 * the execution by the services.
 *
 * @author anton.baukin@gmail.com
 */
public interface EventsDealer
{
	/* Status */

	public static class Status
	{

	}



	/* public: EventsHandler interface */

	public Status deal(Event event);
}