package com.tverts.retrade.domain.invoice;

/* Java */

import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: formatter */

import com.tverts.support.fmt.DocFmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter of Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class InvoiceFmt extends DocFmtBase
{
	protected boolean   isKnown(Object obj)
	{
		return (obj instanceof Invoice);
	}

	protected UnityType getDocType(FmtCtx ctx)
	{
		return Invoices.getInvoiceEffectiveType((Invoice) ctx.obj());
	}

	protected Date      getTime(FmtCtx ctx)
	{
		return ((Invoice) ctx.obj()).getInvoiceDate();
	}
}