package com.tverts.retrade.domain.selset;

/* com.tverts: endure (core + auth) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.auth.AuthLogin;


/**
 * Selection Set Volume is a collection of
 * Selection Set Items.
 *
 * @author anton.baukin@gmail.com
 */
public class SelSet extends NumericBase
{
	/* Selection Set (bean) */

	/**
	 * The login this Selection Set is for.
	 */
	public AuthLogin getLogin()
	{
		return login;
	}

	private AuthLogin login;

	public void setLogin(AuthLogin login)
	{
		this.login = login;
	}

	/**
	 * The name of the Selection Set. The default
	 * set (that may not be deleted, just cleared)
	 * is an empty string.
	 *
	 * The name is unique within the login.
	 * Maximum length is 32 characters.
	 */
	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}
}