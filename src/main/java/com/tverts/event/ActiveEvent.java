package com.tverts.event;

/* com.tverts: actions */

import com.tverts.actions.ActionWithTxBase;


/**
 * Event that may also react on
 * domain actions processing.
 *
 * @author anton.baukin@gmail.com
 */
public interface ActiveEvent extends Event
{
	/* public: ActiveEvent interface */

	public void act(ActionWithTxBase action);
}