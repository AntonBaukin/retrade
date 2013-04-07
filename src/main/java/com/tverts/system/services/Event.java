package com.tverts.system.services;

/**
 * Event that is sent to a {@link Service} instance.
 *
 * There are broadcast events that are sent to each
 * the service registered. The same event instance
 * is given to each of the services in the order
 * of the services dependencies: dependent service
 * receives the event after the referred one.
 *
 * Event must be as Serializable Java Bean.

 * @author anton.baukin@gmail.com
 */
public interface Event extends java.io.Serializable
{
	/* public: Event interface */

	/**
	 * Returns the Service UID this event is for.
	 * Broadcast events has this field undefined.
	 */
	public String getService();

	/**
	 * Event instance may be a container (wrapper)
	 * to some other event. The class of the event
	 * instance may differ from what the event means.
	 * Set the event type (class) when this is true.
	 *
	 * If event type is not defined, the class of
	 * the event instance is the type.
	 */
	public Class  getEventType();
}