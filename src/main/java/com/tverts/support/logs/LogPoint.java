package com.tverts.support.logs;

/**
 * The point to access logging strategies.
 *
 * By default is creates {@link LogStrategyLog4J}
 * instance as Log4J library is the default
 * logging provider of the application.
 *
 * @author anton.baukin@gmail.com
 */
public class LogPoint
{
	/* public: Singleton */

	public static LogPoint getInstance()
	{
		return INSTANCE;
	}

	private static final LogPoint INSTANCE =
	  new LogPoint();

	protected LogPoint()
	{}

	/* public: LogPoint interface */

	public LogStrategy getLogStrategy()
	{
		return logStrategy;
	}

	public void        setLogStrategy(LogStrategy logStrategy)
	{
		if(logStrategy == null)
			throw new IllegalArgumentException();

		this.logStrategy = logStrategy;
	}

	/* public: LogPoint interface */

	private LogStrategy logStrategy =
	  new LogStrategyLog4J();
}