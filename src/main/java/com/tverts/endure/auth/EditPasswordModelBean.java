package com.tverts.endure.auth;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model Bean of editing Login and Password.
 *
 * @author anton.baukin@gmail.com
 */
public class EditPasswordModelBean extends ModelBeanBase
{
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


	/* private: encapsulated data */

	private Long    authLogin;
	private Long    loginDomain;
	private String  loginCode;
	private String  passhash;
	private boolean creating;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		o.writeLong(authLogin);
		o.writeLong(loginDomain);
		IO.str(o, loginCode);
		IO.str(o, passhash);
		o.writeBoolean(creating);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		authLogin   = i.readLong();
		loginDomain = i.readLong();
		loginCode   = IO.str(i);
		passhash    = IO.str(i);
		creating    = i.readBoolean();
	}
}