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
		return (obj instanceof CatItem) ||
		  (obj instanceof NamedEntity) && (obj instanceof CodedEntity);
	}

	protected boolean isFlags(FmtCtx ctx)
	{
		return !ctx.is(EXACT);
	}

	protected String  format(FmtCtx ctx)
	{
		StringBuilder s = new StringBuilder(64);

		String        c = (ctx.obj() instanceof CatItem)
		  ?(((CatItem) ctx.obj()).getCode())
		  :(((CodedEntity) ctx.obj()).getCode());

		String        n = (ctx.obj() instanceof CatItem)
		  ?(((CatItem) ctx.obj()).getName())
		  :(((NamedEntity) ctx.obj()).getName());

		//?: {unity type name}
		if(ctx.is(TYPE) && (ctx.obj() instanceof United))
		{
			Unity     u = ((United) ctx.obj()).getUnity();
			UnityType t = (u == null)?(null):(u.getUnityType());

			if(t != null) if(t.getTitleLo() != null)
				s.append(t.getTitleLo());
			else if(t.getTitle() != null)
				s.append(t.getTitle());

			if(s.length() != 0)
				s.append(' ');
		}

		//~: code
		if(ctx.is(CODE))
			s.append('№').append(c).append(' ');

		//~: name
		s.append(n);

		return s.toString();
	}
}