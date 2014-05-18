package com.tverts.retrade.domain.auth;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.system.AuthLoginsModelData;


/**
 * Model bean for table with views on all
 * the Domain authentication logins.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class AuthLoginsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: AuthLoginsModelBean (bean) interface */

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

	public boolean isPersonsOnly()
	{
		return personsOnly;
	}

	public void setPersonsOnly(boolean personsOnly)
	{
		this.personsOnly = personsOnly;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new AuthLoginsModelData(this);
	}


	/* private: model attributes */

	private String[] searchNames;
	private String   selSet;
	private boolean  personsOnly;
}