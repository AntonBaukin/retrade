package com.tverts.endure.auth;

/* com.tverts: formatter */

import static com.tverts.support.SU.cats;
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
		AuthLogin l = (AuthLogin) ctx.obj();
		return cats("Пользователь [", l.getCode(), "] ", l.getName());
	}
}