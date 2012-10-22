package com.tverts.system.zservices;

/**
 * Service Messager isolates services communications.
 * Messager is always asynchronous. It saves the events
 * in (non-durable) queues and the messaging executor
 * reads that events and invokes the Services System.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface ServiceMessager
{
	/* public: ServiceMessager interface */

	public void sendEvent(Event event);
}