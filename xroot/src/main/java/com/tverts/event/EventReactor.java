package com.tverts.event;

/* com.tverts: support */

import com.tverts.support.misc.Pair;


/**
 * Pair of (Event class, Reactor instance).
 *
 * @author anton.baukin@gmail.com.
 */
public class EventReactor extends Pair<Class<? extends Event>, Reactor>
{
	public EventReactor(Class<? extends Event> event, Reactor reactor)
	{
		super(event, reactor);
	}
}
