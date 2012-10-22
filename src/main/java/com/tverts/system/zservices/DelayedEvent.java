package com.tverts.system.zservices;

/**
 * Delayed Event is an event that must be
 * executed not before the time given.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface DelayedEvent extends Event
{
	/* public: DelayedEvent interface */

	/**
	 * Returns Java-time to execute the event.
	 * Zero value means immediate execution.
	 */
	public long getEventTime();

	public void setEventTime(long time);
}