package com.tverts.retrade.domain.account;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;

/* com.tverts: support */

import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * United formatter of the Payment Ways.
 *
 * @author anton.baukin@gmail.com
 */
public class PayWayFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof PayWay);
	}

	protected String  format(FmtCtx ctx)
	{
		PayWay        w = (PayWay) ctx.obj();
		StringBuilder s = new StringBuilder(92);

		//?: {add type}
		if(ctx.is(TYPE))
			s.append(w.getUnity().getUnityType().getTitleLo());

		//?: {add code}
		if(ctx.is(CODE))
			s.append(" [").append(w.getName()).append("]");

		//~: search for the contractor
		Contractor c = (Contractor) ctx.get(Contractor.class);

		if(!ctx.is(Contractor.class))
			c = bean(GetAccount.class).
			  getPayWayContractor(w.getPrimaryKey());

		if(c != null)
			s.append(" к-та '").append(c.getName()).append('\'');
		else
			s.append(" /Собственный счёт/");


		//?: {no a long format}
		if(!ctx.is(LONG))
			return s.toString();

		//?: {bank payment}
		PayBank b = !(w instanceof PayBank)?(null):((PayBank)w);

		if(b != null) s.
		  append(" №").append(b.getRemitteeAccount()).
		  append(" в ").append(b.getBankName());

		return s.toString();
	}
}