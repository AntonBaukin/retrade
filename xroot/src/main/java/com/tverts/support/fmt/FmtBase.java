package com.tverts.support.fmt;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * Implementation base of an Unified Formatter.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class FmtBase
       implements     FmtRef, FmtUni
{
	/* public: Formatter Reference interface */

	public List<FmtUni> dereferObjects()
	{
		return Collections.<FmtUni> singletonList(this);
	}


	/* public: Unified Formatter interface */

	public String fmt(FmtCtx ctx)
	{
		return !isKnown(ctx)?(null):(format(ctx));
	}


	/* protected: formatting parts */

	protected boolean         isKnown(FmtCtx ctx)
	{
		return isKnown(ctx.obj()) && isFlags(ctx);
	}

	protected boolean         isKnown(Object obj)
	{
		return false;
	}

	protected boolean         isFlags(FmtCtx ctx)
	{
		return true;
	}

	protected abstract String format(FmtCtx ctx);
}