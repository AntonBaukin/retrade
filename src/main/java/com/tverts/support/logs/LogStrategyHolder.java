package com.tverts.support.logs;

/**
 * Combines a {@link LogStrategy} with a holder
 * of the loggers, {@link LoggersHolder}.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class LogStrategyHolder
       extends        LogStrategyBase
{
	/* public: constructror */

	public LogStrategyHolder()
	{
		this.loggersHolder = createDefaultLoggersHolder();
	}

	/* public: LogStrategyHolder interface */

	public LoggersHolder
	            getLoggersHolder()
	{
		return loggersHolder;
	}

	public void setLoggersHolder(LoggersHolder loggersHolder)
	{
		if(loggersHolder == null)
			throw new IllegalStateException();

		this.loggersHolder = loggersHolder;
	}

	/* protected: creating the default holder */

	protected abstract LoggersHolder
	            createDefaultLoggersHolder();

	protected abstract Class
	            getLoggersClass();

	/* protected: LogMethodBase */

	protected abstract class LogMethodBase<L>
	          implements     LogMethod
	{
		/* public: LogMethod interface */

		public boolean isLevel(String d)
		{
			return this.isLevel(logger(d));
		}

		public void    logMsg(String d, CharSequence m)
		{
			this.logMsg(logger(d), m);
		}

		public void    logErr(String d, CharSequence m, Throwable e)
		{
			this.logErr(logger(d), m, e);
		}

		/* protected: loggers invoking */

		@SuppressWarnings("unchecked")
		protected L logger(String d)
		{
			Object l = getLoggersHolder().getLogger(d);

			if(l == null)
				throw new IllegalStateException(String.format(
				  "LogStrategyHolder can't obtain logger instance for " +
				  "the destination '%s'", d));

			if(!getLoggersClass().isAssignableFrom(l.getClass()))
				throw new IllegalStateException(String.format(
				  "LogStrategyHolder for the destination '%s' got the " +
				  "logger of class '%s', but expected of a c'ass '%s' ",
				  d, l.getClass().getName(), getLoggersClass().getName()));

			return (L)l;
		}

		protected abstract boolean
		            isLevel(L logger);

		protected abstract void
		            logMsg(L logger, CharSequence m);

		protected abstract void
		            logErr(L logger, CharSequence m, Throwable e);
	}

	/* private: the holder */

	private LoggersHolder loggersHolder;
}