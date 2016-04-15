package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;


/**
 * {@link EventRun} strategy that clones and sends
 * the {@link Event} configured on receiving
 * {@link SystemReady} event.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class EventRunOnSystemStart extends EventRunDelegate
{
	/* protected: delegating */

	protected boolean isThatEvent(Event event)
	{
		return (event instanceof SystemReady);
	}
}