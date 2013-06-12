package com.tverts.endure.secure;

/* com.tverts: formatter */

import static com.tverts.support.SU.cats;
import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter of Secure package classes.
 *
 * @author anton.baukin@gmail.com
 */
public class SecureFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof SecRule) || (obj instanceof SecSet);
	}

	protected String  format(FmtCtx ctx)
	{
		if(ctx.obj() instanceof SecRule)
			return cats("Правило доступа '",
			  ((SecRule)ctx.obj()).getTitle(), '\'');

		if(ctx.obj() instanceof SecSet)
			return cats("Множество доступа '",
			  ((SecSet)ctx.obj()).getName(), '\'');

		return null;
	}
}