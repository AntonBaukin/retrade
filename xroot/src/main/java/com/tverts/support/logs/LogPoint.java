package com.tverts.support.logs;

/**
 * The point to access logging strategies.
 *
 * By default is creates {@link LogStrategyLog4J}
 * instance as Log4J library is the default
 * logging provider of the application.
 *
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

	public LocalLogStrategy getLogStrategy()
	{
		return strategy;
	}

	/**
	 * Sets the logging strategy given as the fallback
	 * (default) strategy of {@link LocalLogStrategy}.
	 */
	public void setLogStrategy(LogStrategy ls)
	{
		if(ls == null)
			throw new IllegalArgumentException();

		this.strategy = new LocalLogStrategy(ls);
	}


	/* public: LogPoint interface */

	private LocalLogStrategy strategy =
	  new LocalLogStrategy(new LogStrategyLog4J());
}