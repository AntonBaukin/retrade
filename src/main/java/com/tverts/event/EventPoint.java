package com.tverts.event;

/**
 * Point to support and run Events
 * on the domain model processing.
 *
 * @author anton.baukin@gmail.com
 */
public class EventPoint
{
	/* Singleton */

	public static final EventPoint INSTANCE =
	  new EventPoint();

	public static EventPoint getInstance()
	{
		return INSTANCE;
	}


	/* public: EventPoint interface */

	public static void react(Event event)
	{
		INSTANCE.reactEvent(event);
	}

	public void        reactEvent(Event event)
	{
		for(Reactor r : reactor.dereferObjects())
			r.react(event);
	}


	/* public: EventPoint (bean) interface */

	public ReactorRef getReactor()
	{
		return reactor;
	}

	public void setReactor(ReactorRef reactor)
	{
		if(reactor == null) throw new IllegalArgumentException();
		this.reactor = reactor;
	}


	/* reactors reference */

	private ReactorRef reactor = new Reactors();
}