package com.tverts.retrade.domain.invoice;

/* com.tverts: formatter */

import com.tverts.endure.UnityType;
import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Formatter of Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class InvoiceFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof Invoice);
	}

	protected String  format(FmtCtx ctx)
	{
		StringBuilder s = new StringBuilder(64);
		Invoice       i = (Invoice) ctx.obj();
		UnityType     t = Invoices.getInvoiceEffectiveType(i);

		//~: invoice type name
		if(t.getTitleLo() != null)
			s.append(t.getTitleLo());
		else
			s.append(t.getTitle());

		//~: code
		s.append(" [").append(i.getCode()).append("]");

		//?: {long} add date
		if(ctx.is(LONG))
			s.append(" от ").append(DU.datetime2str(i.getInvoiceDate()));

		return s.toString();
	}
}