package com.tverts.support.logs;

/**
 * A strategy to obtain, to store, and to create
 * the logging objects by the destinations.
 *
 * Note that a holder can't remove the loggers.
 *
 * Loggers holder must be thread-safe.
 *
 * @author anton.baukin@gmail.com
 */
public interface LoggersHolder
{
	/* public: LoggersHolder interface */

	/**
	 * Returns the logger by the destination given.
	 * The destination is an abstraction, but at the
	 * most cases it is coded simply as the FQN of
	 * a class or a package.
	 */
	public Object getLogger(String dest);
}