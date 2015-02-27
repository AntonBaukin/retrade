package com.tverts.retrade.domain.auth;

/* Java */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.web.data.system.AuthLoginsModelData;


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
	/* Auth Logins Model (bean) */

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


	/* Model Bean (data access) */

	private boolean personsOnly;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		o.writeBoolean(personsOnly);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		personsOnly = i.readBoolean();
	}
}