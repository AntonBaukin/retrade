package com.tverts.endure.auth;

/* com.tverts: formatter */

import com.tverts.support.fmt.FmtBase;
import com.tverts.support.fmt.FmtCtx;


/**
 * Formatter of Auth logins.
 *
 * @author anton.baukin@gmail.com
 */
public class AuthLoginFmt extends FmtBase
{
	protected boolean isKnown(Object obj)
	{
		return (obj instanceof AuthLogin);
	}

	protected String  format(FmtCtx ctx)
	{
		AuthLogin     l = (AuthLogin) ctx.obj();
		StringBuilder s = new StringBuilder(64);

		if(ctx.is(TYPE))
			s.append("Пользователь ");

		if(ctx.is(CODE))
			s.append('[').append(l.getCode()).append("] ");

		s.append(l.getName());

		return s.toString();
	}
}