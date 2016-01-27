package com.tverts.support.fmt;

/* com.tverts: support */

import com.tverts.support.EX;

/**
 * Deontes an object that implements
 * own formatting.
 *
 * @author anton.baukin@gmail.com.
 */
public interface SelfFmt extends FmtUni
{
	/* Self Formatting */

	/**
	 * Plain variant of formatting that
	 * invokes {@link Object#toString()}.
	 */
	default String fmt(FmtCtx ctx)
	{
		EX.assertx(ctx.obj() == this);
		return this.toString();
	}
}