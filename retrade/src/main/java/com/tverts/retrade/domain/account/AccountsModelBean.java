package com.tverts.retrade.domain.account;

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

import com.tverts.retrade.data.account.AccountsModelData;


/**
 * Model bean for table with views on all the Accounts.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "acounts-model")
public class AccountsModelBean extends DataSelectModelBean
{
	/* Accounts Model */

	public boolean isOwnOnly()
	{
		return ownOnly;
	}

	public void setOwnOnly(boolean ownOnly)
	{
		this.ownOnly = ownOnly;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new AccountsModelData(this);
	}


	/* private: encapsulated data */

	private boolean ownOnly;


	/* Serialization */

	public void writeExternal(ObjectOutput o)
	  throws IOException
	{
		super.writeExternal(o);
		o.writeBoolean(ownOnly);
	}

	public void readExternal(ObjectInput i)
	  throws IOException, ClassNotFoundException
	{
		super.readExternal(i);
		ownOnly = i.readBoolean();
	}
}