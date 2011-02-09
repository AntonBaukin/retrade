package com.tverts.support;

/**
 * Exception handling support.
 *
 * @author anton baukin (abaukin@mail.ru)
 */
public class EX
{
	/* public: other functions */

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
}