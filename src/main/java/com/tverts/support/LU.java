package com.tverts.support;

/* com.tverts: support (logging) */

import com.tverts.support.logs.LogLevel;
import com.tverts.support.logs.LogPoint;

/**
 * Logging support, utilities and wrappers.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class LU
{
	/* define logging levels */

	/**
	 * The maximum possible logging level.
	 * Just refers {@link LogLevel#MAX_LEVEL}.
	 */
	public static final int        MAX_LEVEL =
	  LogLevel.MAX_LEVEL;

	/**
	 * Contains all the levels by their
	 * {@link LogLevel#level()} index.
	 */
	public static final LogLevel[] LEVELS    =
	  LogLevel.LEVELS;


	/* access logging levels */

	/**
	 * Finds the lowest (most detailed) logging level
	 * for the specified destination.
	 *
	 * Note that this method may not return {@code null},
	 * at least it would return {@link LogLevel#ERROR}.
	 */
	public static LogLevel xlevel(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().getMinLevel(dest);
	}

	/**
	 * {@link LogLevel#TRACE} level is enabled?
	 */
	public static boolean  isT(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().isLevel(LogLevel.TRACE, dest);
	}

	/**
	 * {@link LogLevel#DEBUG} level is enabled?
	 */
	public static boolean  isD(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().isLevel(LogLevel.DEBUG, dest);
	}

	/**
	 * {@link LogLevel#INFO} level is enabled?
	 */
	public static boolean  isI(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().isLevel(LogLevel.DEBUG, dest);
	}

	/**
	 * {@link LogLevel#WARN} level is enabled?
	 */
	public static boolean  isW(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().isLevel(LogLevel.WARN, dest);
	}

	/**
	 * {@link LogLevel#WARN} level is enabled?
	 *
	 * (Note that error level is always enabled
	 * in the present implementation.)
	 */
	public static boolean  isE(String dest)
	{
		return true;
	}

	/* logging */

	public static void L(LogLevel level, String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(level, dest, msg);
	}

	public static void L
	  (LogLevel level, String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(level, dest, msg, err);
	}

	public static void T(String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.TRACE, dest, msg);
	}

	public static void T(String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.TRACE, dest, msg, err);
	}

	public static void D(String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.DEBUG, dest, msg);
	}

	public static void D(String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.DEBUG, dest, msg, err);
	}

	public static void I(String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.INFO, dest, msg);
	}

	public static void I(String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.INFO, dest, msg, err);
	}

	public static void W(String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.WARN, dest, msg);
	}

	public static void W(String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.WARN, dest, msg, err);
	}

	public static void E(String dest, CharSequence msg)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.ERROR, dest, msg);
	}

	public static void E(String dest, CharSequence msg, Throwable err)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.ERROR, dest, msg, err);
	}
}