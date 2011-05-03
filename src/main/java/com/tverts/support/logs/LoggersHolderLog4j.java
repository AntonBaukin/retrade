package com.tverts.support.logs;

/* Apache Log4J library */

import org.apache.log4j.Logger;

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
		return Logger.getLogger(dest);
	}
}