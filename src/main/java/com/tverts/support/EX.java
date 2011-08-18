package com.tverts.support;

/* standard Java classes */

import java.io.PrintWriter;

/* com.tverts: supports (streams) */

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
}