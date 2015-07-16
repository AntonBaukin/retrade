package com.tverts.retrade.domain.prices;

/* Java */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: formatter */

import com.tverts.support.fmt.DocFmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter for Price Change Documents.
 *
 * @author anton.baukin@gmail.com.
 */
public class RepriceDocFmt extends DocFmtBase
{
	protected boolean   isKnown(Object obj)
	{
		return (obj instanceof RepriceDoc);
	}

	protected UnityType getDocType(FmtCtx ctx)
	{
		return ((RepriceDoc) ctx.obj()).getUnity().getUnityType();
	}

	protected Date      getTime(FmtCtx ctx)
	{
		return ((RepriceDoc) ctx.obj()).getChangeTime();
	}
}