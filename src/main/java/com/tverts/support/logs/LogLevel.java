package com.tverts.support.logs;

/**
 * Logging levels defined here are equal to
 * Log4J ones as this is the main logging library.
 *
 * The application is designed to have only these
 * levels.
 *
 *
 * @author anton.baukin@gmail.com
 */
public enum LogLevel
{
	/**
	 * Error level is always turned on in the application.
	 * Note that the original logging library may omit
	 * errors when more detailed control is set.
	 */
	ERROR
	{
		public int level()
		{
			return 4;
		}
	},

	/**
	 * The application tells something important.
	 */
	WARN
	{
		public int level()
		{
			return 3;
		}
	},

	/**
	 * The application notes a useful information.
	 */
	INFO
	{
		public int level()
		{
			return 2;
		}
	},

	/**
	 * The application also prints debugging protocols.
	 */
	DEBUG
	{
		public int level()
		{
			return 1;
		}
	},

	/**
	 * The application tells in details what it is doing.
	 * It may also print the debug information here.
	 *
	 * This is the lowest logging priority.
	 */
	TRACE
	{
		public int level()
		{
			return 0;
		}
	};


	/* public: log level integer value */

	/**
	 * The maximum possible logging level.
	 */
	public static final int        MAX_LEVEL = 4;

	/**
	 * Contains all the levels by their
	 * {@link #level()} index.
	 */
	public static final LogLevel[] LEVELS    =
	{
	  TRACE, DEBUG, INFO, WARN, ERROR
	};

	/**
	 * Tells the integer value of the logging level.
	 * The less value is the more details are printed.
	 *
	 * This value may be used as an index of an array
	 * that maps the levels to something.
	 */
	public abstract int level();
}
