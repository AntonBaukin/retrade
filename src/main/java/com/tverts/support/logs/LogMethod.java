package com.tverts.support.logs;

/**
 * Logging method for a predefined log level.
 * Is to be a part of logging strategy.
 *
 * @author anton.baukin@gmail.com
 */
public interface LogMethod
{
	/* public: LogMethod interface */

	/**
	 * Tells that the events of predefined logging level
	 * and the given destination would be actually processed.
	 */
	public boolean isLevel(String d);

	public void    logMsg(String d, CharSequence m);

	public void    logErr(String d, CharSequence m, Throwable e);
}