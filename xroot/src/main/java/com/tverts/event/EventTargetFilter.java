package com.tverts.event;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Reacts on the events where the target
 * is-a configured class.
 *
 * @author anton.baukin@gmail.com
 */
public class EventTargetFilter implements Reactor
{
	/* Reactor */

	public void react(Event event)
	{
		if(OU.isa(event.target(), targetClass))
			if(reactor != null)
				reactor.react(event);
	}


	/* Event Target Filter */

	public Class getTargetClass()
	{
		return targetClass;
	}

	private Class targetClass;

	public void setTargetClass(Class targetClass)
	{
		this.targetClass = targetClass;
	}

	public Reactor getReactor()
	{
		return reactor;
	}

	private Reactor reactor;

	public void setReactor(Reactor reactor)
	{
		this.reactor = reactor;
	}
}
