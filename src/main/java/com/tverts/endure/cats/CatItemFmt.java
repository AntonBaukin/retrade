package com.tverts.endure.cats;

/* com.tverts: endure (core) */

import com.tverts.endure.United;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: formatter */

import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter of Catalogue Items.
 *
 * @author anton.baukin@gmail.com
 */
public class CatItemFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof CatItem);
	}

	protected boolean isFlags(FmtCtx ctx)
	{
		return !ctx.is(EXACT);
	}

	protected String  format(FmtCtx ctx)
	{
		StringBuilder s = new StringBuilder(64);
		CatItem       o = (CatItem) ctx.obj();

		//?: {unity type name}
		if(ctx.is(TYPE) && (o instanceof United))
		{
			Unity     u = ((United)o).getUnity();
			UnityType t = (u == null)?(null):(u.getUnityType());

			if(t != null) if(t.getTitleLo() != null)
				s.append(t.getTitleLo());
			else if(t.getTitle() != null)
				s.append(t.getTitle());

			if(s.length() != 0)
				s.append(' ');
		}

		//~: code
		s.append('№').append(o.getCode()).append(' ');

		//~: name
		s.append(o.getName());

		return s.toString();
	}
}