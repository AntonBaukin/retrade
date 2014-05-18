package com.tverts.retrade.secure;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: model */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade data */

import com.tverts.retrade.data.settings.SecAblesModelData;


/**
 * Model of viewing Auth Login Secure Ables.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class SecAblesModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: SecAblesModelBean (bean) interface */

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

	public String[] getSearchNames()
	{
		return searchNames;
	}

	public void setSearchNames(String[] searchNames)
	{
		this.searchNames = searchNames;
	}

	public String getSelSet()
	{
		return selSet;
	}

	public void setSelSet(String selSet)
	{
		this.selSet = selSet;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new SecAblesModelData(this);
	}


	/* private: model attributes */

	private Long     authLogin;
	private Long     secSet;
	private String[] searchNames;
	private String   selSet;
}