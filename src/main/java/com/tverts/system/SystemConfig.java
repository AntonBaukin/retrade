package com.tverts.system;

/**
 * Provides configuration information global
 * to all the layers of the application.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SystemConfig
{
	/* SystemConfig Singleton */

	public static SystemConfig getInstance()
	{
		return INSTANCE;
	}

	private static final SystemConfig INSTANCE =
	  new SystemConfig();

	protected SystemConfig()
	{}


	/* public: SystemConfig (bean) interface */

	/**
	 * Tells that the application runs in development
	 * (debug) mode. It has the same functions that
	 * may be extended to provide additional support
	 * for the developer.
	 */
	public boolean isDebug()
	{
		return debug;
	}

	public void    setDebug(boolean debug)
	{
		this.debug = debug;
	}


	/* private: system properties */

	private boolean debug;
}