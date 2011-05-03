package com.tverts.support.logs;

/* Apache Log4J library */

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Logging strategy that invokes Log4J loggers.
 *
 * @author anton.baukin@gmail.com
 */
public class   LogStrategyLog4J
       extends LogStrategyHolder
{
	/* protected: LogStrategyHolder interface */

	protected LoggersHolder createDefaultLoggersHolder()
	{
		return new LoggersHolderLog4j();
	}

	protected Class         getLoggersClass()
	{
		return Logger.class;
	}

	/* protected: LogStrategyBase interface */

	protected LogMethod[]   createLogMethods()
	{
		return new LogMethod[]
		{
		  new LMTrace(), new LMDebug(), new LMInfo(),
		  new LMWarn(),  new LMError()
		};
	}

	/* protected: defined methods */

	protected class LMError extends LogMethodBase<Logger>
	{
		protected boolean isLevel(Logger logger)
		{
			return true;
		}

		protected void    logMsg(Logger logger, CharSequence m)
		{
			logger.error(m);
		}

		protected void    logErr(Logger logger, CharSequence m, Throwable e)
		{
			logger.error(m, e);
		}
	}

	protected class LMWarn extends LogMethodBase<Logger>
	{
		protected boolean isLevel(Logger logger)
		{
			return logger.isEnabledFor(Level.WARN);
		}

		protected void    logMsg(Logger logger, CharSequence m)
		{
			logger.warn(m);
		}

		protected void    logErr(Logger logger, CharSequence m, Throwable e)
		{
			logger.warn(m, e);
		}
	}

	protected class LMInfo extends LogMethodBase<Logger>
	{
		protected boolean isLevel(Logger logger)
		{
			return logger.isInfoEnabled();
		}

		protected void    logMsg(Logger logger, CharSequence m)
		{
			logger.info(m);
		}

		protected void    logErr(Logger logger, CharSequence m, Throwable e)
		{
			logger.info(m, e);
		}
	}

	protected class LMDebug extends LogMethodBase<Logger>
	{
		protected boolean isLevel(Logger logger)
		{
			return logger.isDebugEnabled();
		}

		protected void    logMsg(Logger logger, CharSequence m)
		{
			logger.debug(m);
		}

		protected void    logErr(Logger logger, CharSequence m, Throwable e)
		{
			logger.debug(m, e);
		}
	}

	protected class LMTrace extends LogMethodBase<Logger>
	{
		protected boolean isLevel(Logger logger)
		{
			return logger.isTraceEnabled();
		}

		protected void    logMsg(Logger logger, CharSequence m)
		{
			logger.trace(m);
		}

		protected void    logErr(Logger logger, CharSequence m, Throwable e)
		{
			logger.trace(m, e);
		}
	}
}