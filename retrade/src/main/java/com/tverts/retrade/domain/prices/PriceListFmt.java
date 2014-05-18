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


	/* public: PriceListFmt interface */

	public String     parents(FmtCtx ctx)
	{
		StringBuilder s = new StringBuilder(32);

		parents(ctx, s);
		return s.toString();
	}


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

		//?: {longer} add the parents
		if(ctx.is(LONGER) && (l.getParent() != null))
		{
			s.append(" [");
			parents(ctx, s);
			s.append(']');
		}

		return s.toString();
	}

	protected void    parents(FmtCtx ctx, StringBuilder s)
	{
		PriceList l = (PriceList) ctx.obj();
		PriceList p = l.getParent();

		while(p != null)
		{
			if(p != l.getParent()) s.append("/");
			s.append(p.getName());
			p = p.getParent();
		}
	}
}