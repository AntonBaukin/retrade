package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;


/**
 * Defines strategy to handle Service Events.
 *
 * @author anton.baukin@gmail.com
 */
public interface EventRun
{
	/* public: EventRun interface */

	/**
	 * Processes the event and returns true
	 * to break the processing with the other
	 * event handlers registered.
	 *
	 * The order of processing is the order
	 * of handlers registration.
	 */
	public boolean run(Event event);
}