package com.tverts.support;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure core */

import com.tverts.endure.NumericIdentity;

/* com.tverts: support (logging) */

import com.tverts.support.logs.LogLevel;
import com.tverts.support.logs.LogPoint;

/* com.tverts: support  */

import static com.tverts.support.SU.cat;
import static com.tverts.support.SU.lenum;


/**
 * Logging support, utilities and wrappers.
 *
 * @author anton.baukin@gmail.com
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


	/* special logging destinations */

	/**
	 * Destination for debugging purposes.
	 */
	public static final String LOGD =
	  "com.tverts.debug";

	/**
	 * Log destination for performance time measures.
	 */
	public static final String LOGT =
	  "com.tverts.timing";


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
		  getLogStrategy().isLevel(LogLevel.INFO, dest);
	}

	/**
	 * {@link LogLevel#WARN} level is enabled?
	 */
	public static boolean  isW(String dest)
	{
		return LogPoint.getInstance().
		  getLogStrategy().isLevel(LogLevel.WARN, dest);
	}


	/* logging */

	public static void L
	  (LogLevel level, String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(level, dest, cat(msgs));
	}

	public static void L
	  (LogLevel level, String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(level, dest, cat(msgs), err);
	}

	public static void T(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.TRACE, dest, cat(msgs));
	}

	public static void T(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.TRACE, dest, cat(msgs), err);
	}

	public static void D(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.DEBUG, dest, cat(msgs));
	}

	public static void D(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.DEBUG, dest, cat(msgs), err);
	}

	public static void I(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.INFO, dest, cat(msgs));
	}

	public static void I(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.INFO, dest, cat(msgs), err);
	}

	public static void W(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.WARN, dest, cat(msgs));
	}

	public static void W(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.WARN, dest, cat(msgs), err);
	}

	public static void E(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.ERROR, dest, cat(msgs));
	}

	public static void E(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.ERROR, dest, cat(msgs), err);
	}


	/* public: logging support */

	public static String sig(Object obj)
	{
		if(obj == null) return "null";

		if(obj instanceof NumericIdentity)
			return sig((NumericIdentity) obj);

		return SU.cats(
		  obj.getClass().getSimpleName(), '@',
		  SU.i2h(System.identityHashCode(obj))
		);
	}

	public static String sig(NumericIdentity obj)
	{
		if(obj == null) return "null";

		return SU.cats(
		  HiberSystem.getInstance().findActualClass(obj).getSimpleName(), '{',
		  (obj.getPrimaryKey() == null)?("NO KEY"):(obj.getPrimaryKey().toString()),
		  "}@", SU.i2h(System.identityHashCode(obj))
		);
	}

	public static String cls(Object obj)
	{
		return cls((obj == null)?(null):(obj.getClass()));
	}

	public static String cls(Object obj, Class def)
	{
		return cls((obj == null)?(def):(obj.getClass()));
	}

	public static String cls(Class cls)
	{
		return (cls == null)?("undefined"):(cls.getName());
	}


	/* logging issues */

	public static String LB(String base, Class cls)
	{
		StringBuilder s = new StringBuilder(64);

		if(!SU.sXe(base))
		{
			s.append(base);
			if(!base.endsWith("."))
				s.append('.');
		}

		if(s.length() == 0)
			s.append(cls.getName());
		else
			s.append(cls.getSimpleName());

		return s.toString();
	}

	public static String LB(Class base, Class cls)
	{
		String s = base.getName();
		int    i = s.lastIndexOf('.');
		s = (i != -1)?(s.substring(0, i)):(null);

		return LB(s, cls);
	}

	public static String getLogBased(Class base, Object obj)
	{
		return LB(base, obj.getClass());
	}

	public static String td(long initial)
	{
		long   d = System.currentTimeMillis() - initial;
		String z = lenum(3, d % 1000);
		String s = lenum(2, (d / 1000) % 60);
		String m = Long.toString(d / 60000);

		return m + ':' + s + '.' + z;
	}
}