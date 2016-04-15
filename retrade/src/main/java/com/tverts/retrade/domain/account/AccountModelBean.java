package com.tverts.retrade.domain.account;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade models data */

import com.tverts.retrade.web.data.account.AccountModelData;


/**
 * Model bean with main info on an {@link Account}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "account-model")
public class AccountModelBean extends UnityModelBean
{
	/* Account Model (read) */

	public Account accessEntity()
	{
		return (Account) super.accessEntity();
	}


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return new AccountModelData(this);
	}
}