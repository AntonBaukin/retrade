package com.tverts.support.fmt;

/**
 * Unified Formatter interface.
 *
 * @author anton.baukin@gmail.com
 */
public interface FmtUni
{
	/* shared formatting flags */

	/**
	 * Tells to include object type name.
	 */
	public static final Object TYPE  =
	  new Object();

	/**
	 * Tells to include object code.
	 */
	public static final Object CODE  =
	  new Object();

	/**
	 * Allows formatting only by the exact
	 * object type comparing, no abstractions.
	 */
	public static final Object EXACT =
	  new Object();

	/**
	 * Tells to name the object in long variant.
	 */
	public static final Object LONG  =
	  new Object[] { TYPE, CODE };


	/* public: Unified Formatter interface */

	/**
	 * Formats the object of the context according
	 * to the given formatting type. If them are
	 * not known, returns null.
	 */
	public String fmt(FmtCtx ctx);
}