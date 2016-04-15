package com.tverts.system.services.events;

/* com.tverts: system services */

import com.tverts.system.services.Event;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Reacts on {@link SystemReady} event
 * sent to the Main Service broadcasting it.
 *
 * @author anton.baukin@gmail.com
 */
public class   SystemReadyRun
       extends EventRunBase
{
	/* public: EventRun interface */

	public boolean run(Event event)
	{
		if(!(event instanceof SystemReady))
			return false;

		//?: {this event is a broadcast} skip it
		if(broadcasted(event))
			return false;

		//?: {sent to Main} broadcast
		if(mine(event))
		{
			LU.I(getLog(), logsig(), " sending global System Ready!");

			//!: broadcast it
			broadcast(event);

			//HINT: other strategies of Main Service
			// that react on SystemReady do it when
			// it is broadcasted.

			return true;
		}

		return false;
	}
}