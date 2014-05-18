package com.tverts.retrade.domain.payment;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter for Payments.
 *
 * @author anton.baukin@gmail.com
 */
public class PaymentFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof Payment);
	}

	protected String  format(FmtCtx ctx)
	{
		Payment       p = (Payment) ctx.obj();
		StringBuilder s = new StringBuilder(92);

		//?: {add type}
		if(ctx.is(TYPE))
			s.append(p.getUnity().getUnityType().getTitleLo());

		//?: {add code}
		if(ctx.is(CODE))
			s.append(" [").append(p.getCode()).append("]");

		//?: {no a long format}
		if(!ctx.is(LONG))
			return s.toString();

		//~: time
		s.append(" от ").append(DU.datetime2str(p.getTime()));

		//~: order
		PayOrder o = p.getPayOrder();
		s.append(" по ордеру [").append(o.getCode()).append("]");
		s.append(" от ").append(DU.date2str(o.getTime()));

		return s.toString();
	}
}