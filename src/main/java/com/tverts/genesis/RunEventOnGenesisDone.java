package com.tverts.genesis;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.events.EventBase;
import com.tverts.system.services.events.EventRunDelegate;

/* com.tverts: self-shunt service */

import com.tverts.shunts.service.SelfShuntEvent;


/**
 * Responds on {@link GenesisDone} event
 * by sending the event configured.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   RunEventOnGenesisDone
       extends EventRunDelegate
{
	/* protected: delegating */

	protected boolean   isThatEvent(Event event)
	{
		return (event instanceof GenesisDone);
	}

	protected EventBase updateEvent(EventBase res, Event event)
	{
		if(res instanceof SelfShuntEvent)
			((SelfShuntEvent)res).setDomain(((GenesisDone)event).getDomain());

		return res;
	}
}