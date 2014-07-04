package com.tverts.api.support;

/* Java */

import java.util.Collection;


/**
 * Exception and assertions handling support.
 */
public class EX
{
	/* assertions */

	public static void   assertx(boolean x, Object... msg)
	{
		if(x == false)
			throw ass(msg);
	}

	public static <T> T  assertn(T x, Object... msg)
	{
		if(x == null)
			throw ass(msg);
		return x;
	}

	@SuppressWarnings("unchecked")
	public static void   asserte(Collection c, Object... msg)
	{
		if((c == null) || c.isEmpty())
			throw ass(msg);
	}

	public static void   asserte(Object[] a, Object... msg)
	{
		if((a == null) || (a.length == 0))
			throw ass(msg);
	}

	public static String asserts(String s, Object... msg)
	{
		if((s == null) || (s.length() != s.trim().length()))
			throw ass(msg);
		return s;
	}


	/* exceptions */

	public static AssertionError ass(Object... msg)
	{
		StringBuilder sb = new StringBuilder(32);
		cat(sb, msg);
		String        s  = sb.toString().trim();

		if(s.isEmpty())
			return new AssertionError();
		else
			return new AssertionError(s);
	}


	/* private: support */

	@SuppressWarnings("unchecked")
	private static void cat(StringBuilder s, Collection objs)
	{
		for(Object o : objs)
			if(o instanceof Collection)
				cat(s, (Collection) o);
			else if(o instanceof Object[])
				cat(s, (Object[]) o);
			else if(o != null)
				s.append(o);
	}

	private static void cat(StringBuilder s, Object[] objs)
	{
		for(Object o : objs)
			if(o instanceof Collection)
				cat(s, (Collection)o);
			else if(o instanceof Object[])
				cat(s, (Object[]) o);
			else if(o != null)
				s.append(o);
	}
}