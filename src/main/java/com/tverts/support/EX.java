package com.tverts.support;

/* standard Java classes */

import java.io.PrintWriter;
import java.util.Collection;

/* com.tverts: secure */

import com.tverts.secure.ForbiddenException;

/* com.tverts: supports */

import com.tverts.support.logs.TransparentException;
import com.tverts.support.streams.StringBuilderWriter;


/**
 * Exception handling support.
 *
 * @author anton.baukin@gmail.com
 */
public class EX
{
	/* other functions */

	/**
	 * Finds the text of the exception. Useful
	 * when original exception is wrapped in
	 * exceptions without text.
	 */
	public static String    e2en(Throwable e)
	{
		String r = null;

		while((r == null) && (e != null))
		{
			r = SU.s2s(e.getMessage());
			if(r == null) e = e.getCause();
		}

		return r;
	}

	/**
	 * The same as {@link #e2en(Throwable)},
	 * but searches for localized version.
	 */
	public static String    e2lo(Throwable e)
	{
		String r = null;

		while((r == null) && (e != null))
		{
			r = SU.s2s(e.getLocalizedMessage());
			if(r == null) e = e.getCause();
		}

		return r;
	}


	/* public: unwrapping  */

	/**
	 * Removes the {@link RuntimeException} wrappers.
	 */
	public static Throwable xrt(Throwable e)
	{
		while(e instanceof RuntimeException)
			if(e.getCause() == null)
				return e;
			else
				e = e.getCause();

		return e;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E
	                        search(Throwable e, Class<E> eclass)
	{
		while(e != null)
			if(eclass.isAssignableFrom(e.getClass()))
				return (E)e;
			else
				e = e.getCause();

		return null;
	}

	public static boolean   isTransparent(Throwable e)
	{
		while(e != null)
			if(TransparentException.class.isAssignableFrom(e.getClass()))
				return true;
			else
				e = e.getCause();

		return false;
	}


	/* error printing */

	public static String    print(Throwable e)
	{
		StringBuilder s = new StringBuilder(512);

		print(e, s);
		return s.toString();
	}

	public static void      print(Throwable e, StringBuilder s)
	{
		String m = e2en(e);

		if(m != null)
			s.append(m).append('\n');

		PrintWriter p = new PrintWriter(new StringBuilderWriter(s), false);

		e.printStackTrace(p);
		p.flush();
	}


	/* assertions */

	public static void  assertx(boolean x, Object... msg)
	{
		if(!x) throw new AssertionError(SU.cats(msg));
	}

	public static <T> T assertn(T x, Object... msg)
	{
		if(x == null) throw new AssertionError(SU.cats(msg));
		return x;
	}

	public static void  asserte(Collection c, Object... msg)
	{
		if((c == null) || c.isEmpty())
			throw new AssertionError(SU.cats(msg));
	}

	public static void  asserte(Object[] a, Object... msg)
	{
		if((a == null) || (a.length == 0))
			throw new AssertionError(SU.cats(msg));
	}


	/* exceptions */

	public static RuntimeException state(Object... msg)
	{
		return new IllegalStateException(SU.cats(msg));
	}

	public static RuntimeException state(Throwable cause, Object... msg)
	{
		return new IllegalStateException(SU.cats(msg), cause);
	}

	public static RuntimeException arg(Object... msg)
	{
		return new IllegalArgumentException(SU.cats(msg));
	}

	public static RuntimeException arg(Throwable cause, Object... msg)
	{
		return new IllegalArgumentException(SU.cats(msg), cause);
	}

	public static RuntimeException forbid(Object... msg)
	{
		return new ForbiddenException(SU.s2s(SU.cats(msg)));
	}

	public static RuntimeException unop(Object... msg)
	{
		return new UnsupportedOperationException(SU.s2s(SU.cats(msg)));
	}

	public static RuntimeException wrap(Throwable cause, Object... msg)
	{
		String s = SU.s2s(SU.cats(msg));
		if(s == null) s = e2en(cause);

		return new RuntimeException(s, cause);
	}
}