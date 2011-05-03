package com.tverts.support.logs;

/* standard Java classes */

import java.util.Map;
import java.util.HashMap;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;

/**
 * Implements synchronous operations of a holder.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class LoggersHolderBase
       implements     LoggersHolder
{
	/* public: constructor */

	public LoggersHolderBase()
	{
		this.loggers = createLoggersMap();
		this.mutex   = obtainStoreMutex();
	}

	/* public: LoggersHolder interface */

	public Object getLogger(String dest)
	{
		if((dest = s2s(dest)) == null)
			throw new IllegalArgumentException();

		synchronized(mutex)
		{
			Object logger = this.loggers.get(dest);

			if(logger == null)
				this.loggers.put(dest,
				  logger = obtainLogger(dest));

			return logger;
		}
	}

	/* protected: synchronous storing */

	protected abstract Object
	                 obtainLogger(String dest);

	protected Map<String, Object>
	                 createLoggersMap()
	{
		return new HashMap<String, Object>(31);
	}

	protected Object obtainStoreMutex()
	{
		return new Object();
	}

	/* protected: the store */

	protected final Map<String, Object> loggers;
	protected final Object              mutex;
}