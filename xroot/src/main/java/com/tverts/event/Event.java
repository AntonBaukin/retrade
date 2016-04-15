package com.tverts.event;

/**
 * Event of the domain model lifecycle.
 * Note that such events has no relation
 * to System Services Events.
 *
 * Events are not thread-safe and
 * are processed by one thread only.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Event
{
	/* public: Event interface */

	/**
	 * Object or entity associated with this event.
	 */
	public Object  target();

	/**
	 * Returns the event data by it's key.
	 */
	public Object  get(Object key);

	public Object  put(Object key, Object val);

	/**
	 * Index of processing cycle. Starts
	 * with 0 for initial processing.
	 */
	public int     cycle();

	/**
	 * Request new processing cycle.
	 * The event system would repeat
	 * this event instance.
	 *
	 * Allows events processors to
	 * communicate.
	 */
	public void    recycle();

	/**
	 * Tells that a new cycle was requested.
	 * Event may not support several cycles,
	 * and this flag is always false for them.
	 */
	public boolean isRecycled();

	/**
	 * This call terminates processing of the current
	 * cycle. New cycle may be started if requested.
	 */
	public void    commit();

	/**
	 * Returns the text to write to the log,
	 * or null not to log the event.
	 */
	public String  logText();
}