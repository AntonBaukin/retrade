package com.tverts.retrade.domain.goods;

/* com.tverts: formatter */

import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter for Good Units.
 *
 * @author anton.baukin@gmail.com.
 */
public class GoodFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof GoodUnit);
	}

	protected String  format(FmtCtx ctx)
	{
		GoodUnit      g = (GoodUnit) ctx.obj();
		StringBuilder s = new StringBuilder(64);

		s.append(g.isService()?("Услуга "):("Товар "));

		//~: code
		if(ctx.is(CODE))
			s.append('№').append(g.getCode()).append('-');

		//~: measure unit
		s.append(g.getMeasure().getCode()).append(' ');

		//~: name
		s.append(g.getName());

		return s.toString();
	}
}