package com.tverts.retrade.secure;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (data) */

import com.tverts.retrade.web.data.settings.SecAblesModelData;

/* com.tverts: support */

import com.tverts.support.IO;


/**
 * Model of viewing Auth Login Secure Ables.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "secure-ables-model")
public class SecAblesModelBean extends DataSelectModelBean
{
	/* Secure Ables Model  */

	public Long getAuthLogin()
	{
		return authLogin;
	}

	public void setAuthLogin(Long authLogin)
	{
		this.authLogin = authLogin;
	}

	public Long getSecSet()
	{
		return secSet;
	}

	public void setSecSet(Long secSet)
	{
		this.secSet = secSet;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new SecAblesModelData(this);
	}


	/* private: encapsulated data */

	private Long authLogin;
	private Long secSet;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);

		IO.longer(o, authLogin);
		IO.longer(o, secSet);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);

		authLogin = IO.longer(i);
		secSet    = IO.longer(i);
	}
}