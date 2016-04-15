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

	/**
	 * Invoked before this event processing.
	 */
	public void actBefore(ActionWithTxBase action);

	/**
	 * Invoked after this event processing.
	 * Error argument is defined in the case of error.
	 */
	public void actAfter(Throwable error);
}