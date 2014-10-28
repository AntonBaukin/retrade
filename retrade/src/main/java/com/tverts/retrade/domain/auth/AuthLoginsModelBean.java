package com.tverts.retrade.domain.auth;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
@XmlType(name = "auth-logins-model")
public class AuthLoginsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: AuthLoginsModelBean (bean) interface */

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

	private boolean  personsOnly;
}