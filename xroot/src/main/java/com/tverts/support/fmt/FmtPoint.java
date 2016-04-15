package com.tverts.support.fmt;

/* Java */

import java.util.List;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Root Unified Formatter.
 *
 * @author anton.baukin@gmail.com
 */
public class FmtPoint implements FmtUni
{
	/* Singleton */

	public static final FmtPoint INSTANCE =
	  new FmtPoint();

	public static FmtPoint getInstance()
	{
		return INSTANCE;
	}


	/* public: static interface */

	public static String format(Object obj, Object... flags)
	{
		FmtCtx ctx = new FmtCtx().obj(obj);

		for(Object flag : flags)
			ctx.set(flag);

		return INSTANCE.fmt(ctx);
	}

	public static String format(FmtCtx ctx)
	{
		return INSTANCE.fmt(ctx);
	}


	/* public: FmtUni interface */

	public String fmt(FmtCtx ctx)
	{
		String res;

		//?: {target is self-formatter}
		if(ctx.obj() instanceof FmtUni)
		{
			res = ((FmtUni)ctx.obj()).fmt(ctx);
			if(!SU.sXe(res)) return res;
		}

		//~: proceed with sequence...
		List<FmtUni> fmts = null;

		if(reference != null)
			fmts = reference.dereferObjects();

		if(fmts != null) for(FmtUni fmt : fmts)
		{
			res = fmt.fmt(ctx);
			if(!SU.sXe(res)) return res;
		}

		return null;
	}


	/* public: FmtPoint (bean) interface */

	public FmtRef getReference()
	{
		return reference;
	}

	public void setReference(FmtRef reference)
	{
		this.reference = reference;
	}


	/* private: formatter reference */

	private FmtRef reference;
}