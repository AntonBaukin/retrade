package com.tverts.endure.secure;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.auth.AuthLogin;


/**
 * Link instance between {@link AuthLogin}
 * user login entity and the Security Rule
 * instance that the user is assigned for.
 *
 * Link presence tells that the user is able
 * to do some of actions provided by the Rule.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class SecAble extends NumericBase
{
	/* public: SecAble (bean) interface */

	public AuthLogin getLogin()
	{
		return login;
	}

	public void setLogin(AuthLogin login)
	{
		this.login = login;
	}

	public SecRule getRule()
	{
		return rule;
	}

	public void setRule(SecRule rule)
	{
		this.rule = rule;
	}


	/* linked entities */

	private AuthLogin login;
	private SecRule   rule;
}