package com.tverts.support.logs;

/* Logging for Java */

import org.apache.logging.log4j.LogManager;


/**
 * Loggers holder for Log4J loggers.
 *
 * @author anton.baukin@gmail.com
 */
public class   LoggersHolderLog4j
       extends LoggersHolderBase
{
	/* protected: LoggersHolderBase interface  */

	protected Object obtainLogger(String dest)
	{
		return LogManager.getLogger(dest);
	}
}