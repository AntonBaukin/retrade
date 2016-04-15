package com.tverts.support.logs;

/**
 * Dispatches events with logging levels
 * to the the logging strategies aggregated.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class LogStrategyBase
       implements     LogStrategy
{
	/* public: constructor */

	public LogStrategyBase()
	{
		this.methods = createLogMethods();

		for(LogMethod m : this.methods)
			if(m == null) throw new IllegalStateException();
	}


	/* public: LogStrategy interface */

	public boolean  isLevel(LogLevel l, String d)
	{
		return this.methods[l.level()].isLevel(d);
	}

	public void     logMsg(LogLevel l, String d, CharSequence m)
	{
		this.methods[l.level()].logMsg(d, m);
	}

	public void     logErr(LogLevel l, String d, CharSequence m, Throwable e)
	{
		this.methods[l.level()].logErr(d, m, e);
	}

	public LogLevel getMinLevel(String d)
	{
		for(int l = 0;(l < this.methods.length);l++)
			if(this.methods[l].isLevel(d))
				return LogLevel.LEVELS[l];

		throw new  IllegalStateException(
		  "LogStrategyBase was unable to define the minimal " +
		  "logging level, but at least ERROR level must be allowed!");
	}


	/* protected: creating the strategies */

	/**
	 * Creates the map of {@link LogLevel}s integer values
	 * to the logging methods. Note that {@code null}
	 * entries are forbidden.
	 */
	protected abstract LogMethod[] createLogMethods();


	/* protected: the strategies */

	protected final LogMethod[]    methods;
}