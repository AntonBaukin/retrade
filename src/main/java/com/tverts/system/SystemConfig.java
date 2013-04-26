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

	public int     getDumpLimit()
	{
		return (dumpLimit == 0)?(512):(dumpLimit);
	}

	public void    setDumpLimit(int dumpLimit)
	{
		if(dumpLimit < 0) throw new IllegalArgumentException();
		this.dumpLimit = dumpLimit;
	}

	/**
	 * Default grid size.
	 */
	public int     getGridSize()
	{
		return (gridSize != 0)?(gridSize):(25);
	}

	public void    setGridSize(int gridSize)
	{
		if(gridSize < 1) throw new IllegalArgumentException();
		this.gridSize = gridSize;
	}

	/**
	 * Defines the default length of Login Sessions
	 * in minutes. Default is 120 (2 hours).
	 */
	public int     getSessionTime()
	{
		return (sessionTime == 0)?(120):(sessionTime);
	}

	public void    setSessionTime(int st)
	{
		if(st < 0) throw new IllegalArgumentException();
		this.sessionTime = st;
	}


	/* private: system properties */

	private boolean debug;
	private int     dumpLimit;
	private int     gridSize;
	private int     sessionTime;
}