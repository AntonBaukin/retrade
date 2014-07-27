package com.tverts.retrade.domain.prices;

/* com.tverts: formatter */

import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formats a Price List.
 *
 * @author anton.baukin@gmail.com.
 */
public class PriceListFmt extends FmtBase
{
	public static final PriceListFmt INSTANCE =
	  new PriceListFmt();


	/* protected: formatting */

	protected boolean isKnown(Object obj)
	{
		return (obj instanceof PriceList);
	}

	protected String  format(FmtCtx ctx)
	{
		StringBuilder s = new StringBuilder(64);
		PriceList     l = (PriceList) ctx.obj();

		//~: price list name
		s.append(l.getName());

		//~: code
		if(ctx.is(CODE))
		s.append(" №").append(l.getCode());

		return s.toString();
	}
}