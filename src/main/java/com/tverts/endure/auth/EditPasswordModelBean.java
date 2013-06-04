package com.tverts.endure.auth;

import com.tverts.model.ModelBeanBase;

/**
 * Model Bean of editing Login and Password.
 *
 * @author anton.baukin@gmail.com
 */
public class EditPasswordModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: EditPasswordModelBean (bean) interface */

	public Long getAuthLogin()
	{
		return authLogin;
	}

	public void setAuthLogin(Long authLogin)
	{
		this.authLogin = authLogin;
	}

	public Long getLoginDomain()
	{
		return loginDomain;
	}

	public void setLoginDomain(Long loginDomain)
	{
		this.loginDomain = loginDomain;
	}

	public String getLoginCode()
	{
		return loginCode;
	}

	public void setLoginCode(String loginCode)
	{
		this.loginCode = loginCode;
	}

	public String getPasshash()
	{
		return passhash;
	}

	public void setPasshash(String passhash)
	{
		this.passhash = passhash;
	}

	public boolean isCreating()
	{
		return creating;
	}

	public void setCreating(boolean creating)
	{
		this.creating = creating;
	}


	/* private: attributes */

	private Long    authLogin;
	private Long    loginDomain;
	private String  loginCode;
	private String  passhash;
	private boolean creating;
}