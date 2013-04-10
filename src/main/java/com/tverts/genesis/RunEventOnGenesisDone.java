package com.tverts.genesis;

/* com.tverts: system services */

import com.tverts.system.services.Event;
import com.tverts.system.services.events.EventRunDelegate;


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

	protected boolean isThatEvent(Event event)
	{
		return (event instanceof GenesisDone);
	}
}