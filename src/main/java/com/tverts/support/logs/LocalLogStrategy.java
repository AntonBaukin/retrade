package com.tverts.support.logs;

/**
 * This special logging strategy is exposed
 * directly by {@link LogPoint} and may not
 * be overwritten there.
 *
 * It allows to attach thread-bound logging
 * strategy, and uses fallback strategy as
 * the default.
 *
 *
 * @author anton.baukin@gmail.com
 */
public final class LocalLogStrategy implements LogStrategy
{
	/* public: constructor */

	public LocalLogStrategy(LogStrategy fallback)
	{
		if(fallback == null)
			throw new IllegalArgumentException();
		this.fallback = fallback;
	}


	/* public: LocalLogStrategy interface */

	/**
	 * The fallback strategy is defined on the system level
	 * and shared between all the threads. It is a default
	 * system logger to application log files.
	 */
	public LogStrategy getFallback()
	{
		return fallback;
	}

	/**
	 * Returns the thread-bound logger.
	 */
	public LogStrategy get()
	{
		return locals.get();
	}

	/**
	 * Replaces the logging strategy bound to the thread.
	 * Note that the fallback strategy is not used after
	 * this call. To unbind pass undefined strategy.
	 */
	public void        set(LogStrategy ls)
	{
		if(ls == null)
			locals.remove();
		else
			locals.set(ls);
	}

	public LogStrategy tee()
	{
		LogStrategy ls = this.get();
		return !(ls instanceof TeeLog)?(null):(((TeeLog)ls).a());
	}

	public void        tee(LogStrategy ls)
	{
		if(ls == null)
			this.set(null);
		else
			this.set(new TeeLog(ls, fallback));
	}


	/* public: LogStrategy interface */

	public boolean  isLevel(LogLevel l, String d)
	{
		LogStrategy ls = locals.get();
		if(ls == null) ls = fallback;

		return ls.isLevel(l, d);
	}

	public void     logMsg(LogLevel l, String d, CharSequence m)
	{
		LogStrategy ls = locals.get();
		if(ls == null) ls = fallback;

		ls.logMsg(l, d, m);
	}

	public void     logErr(LogLevel l, String d, CharSequence m, Throwable e)
	{
		LogStrategy ls = locals.get();
		if(ls == null) ls = fallback;

		ls.logErr(l, d, m, e);
	}

	public LogLevel getMinLevel(String d)
	{
		LogStrategy ls = locals.get();
		if(ls == null) ls = fallback;

		return ls.getMinLevel(d);
	}


	/* private: fallback strategy */

	private LogStrategy fallback;


	/* private: thread local storage */

	private ThreadLocal<LogStrategy> locals =
	  new ThreadLocal<LogStrategy>();
}