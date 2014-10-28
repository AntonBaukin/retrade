package com.tverts.retrade.domain.account;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.account.PayWaysModelData;


/**
 * Extends Accounts table model to return Payment Ways
 * instances instead of the Accounts.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "payment-ways-model")
public class PayWaysModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public boolean isOwnOnly()
	{
		return ownOnly;
	}

	public void setOwnOnly(boolean ownOnly)
	{
		this.ownOnly = ownOnly;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PayWaysModelData(this);
	}


	/* private: model attributes */

	private boolean ownOnly;
}