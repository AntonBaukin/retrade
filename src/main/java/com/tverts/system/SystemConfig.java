package com.tverts.system;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides configuration information global
 * to all the layers of the application.
 *
 * @author anton.baukin@gmail.com
 */
public class SystemConfig
{
	/* System Configuration Singleton */

	public static SystemConfig getInstance()
	{
		return INSTANCE;
	}

	public static final SystemConfig INSTANCE =
	  new SystemConfig();

	protected SystemConfig()
	{}


	/* General Configuration */

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

	private boolean debug;

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * Defines the default length of Login Sessions
	 * in minutes. Default is 120 (2 hours).
	 */
	public int getSessionTime()
	{
		return (sessionTime == 0)?(120):(sessionTime);
	}

	private int sessionTime;

	public void setSessionTime(int st)
	{
		EX.assertx(st >= 0);
		this.sessionTime = st;
	}

	/**
	 * General parameter of number of milliseconds
	 * that some internal components may wait to
	 * check and invalidate internal state.
	 *
	 * In the debug mode defaults to 2 seconds, in
	 * normal mode is always 10-times longer.
	 */
	public int getCheckInterval()
	{
		return (debug)?(checkInterval):(checkInterval * 10);
	}

	private int checkInterval = 2000;

	public void setCheckInterval(int checkInterval)
	{
		EX.assertx(checkInterval >= 100);
		this.checkInterval = checkInterval;
	}


/* Database Access */

	public int getDumpLimit()
	{
		return (dumpLimit == 0)?(512):(dumpLimit);
	}

	private int dumpLimit;

	public void setDumpLimit(int dumpLimit)
	{
		EX.assertx(dumpLimit >= 0);
		this.dumpLimit = dumpLimit;
	}


	/* User Interface */

	/**
	 * Default UI grid size.
	 */
	public int getGridSize()
	{
		return (gridSize != 0)?(gridSize):(20);
	}

	private int gridSize;

	public void setGridSize(int gridSize)
	{
		EX.assertx(gridSize >= 1);
		this.gridSize = gridSize;
	}

	/**
	 * The page size of the UI Events messages list.
	 * Defaults to the 5 items.
	 */
	public int getUserEventsPage()
	{
		return (userEventsPage == 0)?(10):(userEventsPage);
	}

	private int userEventsPage;

	public void setUserEventsPage(int userEventsPage)
	{
		this.userEventsPage = userEventsPage;
	}

	/**
	 * The number of UI Events messages to select by the right
	 * and the left sides of the current page. Defaults to 2 pages.
	 */
	public int getUserEventsFetch()
	{
		return (userEventsFetch != 0)?(userEventsFetch):(2*getUserEventsPage());
	}

	private int userEventsFetch;

	public void setUserEventsFetch(int userEventsFetch)
	{
		this.userEventsFetch = userEventsFetch;
	}

	public String getGoPagePrefix()
	{
		return goPagePrefix;
	}

	private String goPagePrefix = "/go/";

	public void setGoPagePrefix(String goPagePrefix)
	{
		this.goPagePrefix = goPagePrefix;
	}
}