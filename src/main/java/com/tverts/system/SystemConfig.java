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
	/* SystemConfig Singleton */

	public static SystemConfig getInstance()
	{
		return INSTANCE;
	}

	public static final SystemConfig INSTANCE =
	  new SystemConfig();

	protected SystemConfig()
	{}


	/* System Configuration */

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

	/**
	 * Default grid size.
	 */
	public int getGridSize()
	{
		return (gridSize != 0)?(gridSize):(25);
	}

	private int gridSize;

	public void setGridSize(int gridSize)
	{
		EX.assertx(gridSize >= 1);
		this.gridSize = gridSize;
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
	 * The page size of the UI Events messages list.
	 * Defaults to the 5 items.
	 */
	public int getUserEventsPage()
	{
		return (userEventsPage == 0)?(5):(userEventsPage);
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
}