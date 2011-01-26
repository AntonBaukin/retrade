package com.tverts.support.logs;

/**
 * A strategy to write actual logging events.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public interface LogStrategy
{
	/* public: LogStrategy interface */

	/**
	 * Tells that the events of given logging level
	 * and the destination would be actually processed.
	 */
	public boolean  isLevel(LogLevel l, String d);

	public void     logMsg(LogLevel l, String d, CharSequence m);

	public void     logErr(LogLevel l, String d, CharSequence m, Throwable e);

	/**
	 * Finds the lowest (most detailed) logging level
	 * for the specified destination.
	 *
	 * Note that this method may not return {@code null},
	 * at least it would return {@link LogLevel#ERROR}.
	 */
	public LogLevel getMinLevel(String d);
}