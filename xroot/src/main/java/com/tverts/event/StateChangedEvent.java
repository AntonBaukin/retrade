package com.tverts.event;

/* com.tverts: endure */

import com.tverts.endure.StatefulEntity;


/**
 * Triggered after the state of Entity had changed.
 *
 * @author anton.baukin@gmail.com
 */
public class StateChangedEvent extends EventBase
{
	/* public: constructor */

	public StateChangedEvent(StatefulEntity target)
	{
		super(target);
	}
}