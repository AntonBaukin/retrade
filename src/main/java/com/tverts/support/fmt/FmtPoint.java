package com.tverts.support.fmt;

/* standard Java classes  */

import java.util.List;


/**
 * Root Unified Formatter.
 *
 * @author anton.baukin@gmail.com
 */
public class FmtPoint implements FmtUni
{
	/* Singletone */

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
		List<FmtUni> fmts = null;

		if(reference != null)
			fmts = reference.dereferObjects();

		if(fmts != null) for(FmtUni fmt : fmts)
		{
			String res = fmt.fmt(ctx);
			if(res != null) return res;
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