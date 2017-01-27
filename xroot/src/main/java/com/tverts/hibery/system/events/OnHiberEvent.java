package com.tverts.hibery.system.events;

/**
 * Basic interface reacting on hibernate events.
 *
 * @author anton.baukin@gmail.com
 */
public interface OnHiberEvent
{
	/* public: OnEvent interface */

	public void onHiberEvent(Object e);
}