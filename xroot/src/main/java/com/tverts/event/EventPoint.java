package com.tverts.event;

/* com.tverts: support */

import com.tverts.support.LU;
import com.tverts.support.SU;


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


	/* public: log destinations */

	public static final String LOG_EVENTS =
	  "com.tverts.event";


	/* public: EventPoint interface */

	public static void react(Event event)
	{
		INSTANCE.reactEvent(event);
	}

	public void        reactEvent(Event event)
	{
		//~: log
		if(LU.isD(LOG_EVENTS))
		{
			String log = event.logText();
			if(!SU.sXe(log)) LU.D(LOG_EVENTS, log);
		}

		//~: call the reactors
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