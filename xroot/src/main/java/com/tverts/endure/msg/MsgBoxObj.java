package com.tverts.endure.msg;

/* com.tverts: endure (core, auth) */

import com.tverts.endure.OxNumericTxBase;
import com.tverts.endure.auth.AuthLogin;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Message Box relates to an {@link AuthLogin}.
 * It is a container of messages sent to the related login.
 *
 * @author anton.baukin@gmail.com
 */
public class MsgBoxObj extends OxNumericTxBase
{
	/* Object Extraction */

	public MsgBox getOx()
	{
		MsgBox ox = (MsgBox) super.getOx();
		if(ox == null) setOx(ox = new MsgBox());
		return ox;
	}

	public void   setOx(Object ox)
	{
		EX.assertx(ox instanceof MsgBox);
		super.setOx(ox);
	}


	/* Message Box */

	public AuthLogin getLogin()
	{
		return login;
	}

	private AuthLogin login;

	public void setLogin(AuthLogin login)
	{
		this.login = login;
	}
}