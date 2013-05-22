package com.tverts.event;

/**
 * System lifecycle event processing strategy.
 *
 * @author anton.baukin@gmail.com
 */
public interface Reactor
{
	/* public: Reactor interface */

	public void react(Event event);
}