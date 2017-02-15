package com.tverts.support;

/* com.tverts: hibery */

import com.tverts.hibery.system.HiberSystem;

/* com.tverts: endure core */

import com.tverts.endure.NumericIdentity;

/* com.tverts: support (logging) */

import com.tverts.support.logs.LogLevel;
import com.tverts.support.logs.LogPoint;


/**
 * Logging support, utilities and wrappers.
 *
 * @author anton.baukin@gmail.com
 */
public class LU
{
	/* Special Logging Destinations */

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


	/* Access Logging Levels */

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

	public static void D(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.DEBUG, dest, SU.cat(msgs));
	}

	public static void D(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.DEBUG, dest, SU.cat(msgs), err);
	}

	public static void I(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.INFO, dest, SU.cat(msgs));
	}

	public static void I(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.INFO, dest, SU.cat(msgs), err);
	}

	public static void W(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.WARN, dest, SU.cat(msgs));
	}

	public static void W(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.WARN, dest, SU.cat(msgs), err);
	}

	public static void E(String dest, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logMsg(LogLevel.ERROR, dest, SU.cat(msgs));
	}

	public static void E(String dest, Throwable err, Object... msgs)
	{
		LogPoint.getInstance().getLogStrategy().
		  logErr(LogLevel.ERROR, dest, SU.cat(msgs), err);
	}


	/* public: logging support */

	public static String sig(Object obj)
	{
		if(obj == null) return "null";

		if(obj instanceof NumericIdentity)
			return sig((NumericIdentity) obj);

		return SU.cats(
		  obj.getClass().getSimpleName(), '#',
		  SU.i2h(System.identityHashCode(obj))
		);
	}

	public static String sig(NumericIdentity obj)
	{
		if(obj == null) return "null";

		return SU.cats(
		  HiberSystem.getInstance().findActualClass(obj).getSimpleName(), "[",
		  (obj.getPrimaryKey() == null)?("?"):(obj.getPrimaryKey().toString()),
		  "]@", SU.i2h(System.identityHashCode(obj))
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
		return (cls == null)?("undefined"):(cls.getSimpleName());
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

	/**
	 * Prints time delta in the format: mm:ss.mss.
	 * Time relates to the current time.
	 */
	public static String td(long initial)
	{
		StringBuilder s = new StringBuilder(10);
		td(s, System.currentTimeMillis() - initial);
		return s.toString();
	}

	/**
	 * Prints time delta in the format: mm:ss.mss.
	 * The time expected is delta time.
	 */
	public static void   td(StringBuilder s, long dt)
	{
		//~: minutes
		SU.lennum((int)(dt / 60000), 3, s);
		s.append(':');

		//~: seconds
		SU.lennum((int)(dt/1000%60), 2, s);
		s.append('.');

		//~: milliseconds
		SU.lennum((int)(dt%1000), 3, s);
	}
}