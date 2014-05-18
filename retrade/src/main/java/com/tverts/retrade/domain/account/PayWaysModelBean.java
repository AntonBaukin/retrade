package com.tverts.retrade.domain.account;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ObjectsSelectModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.account.PayWaysModelData;


/**
 * Extends Accounts table model to return Payment Ways
 * instances instead of the Accounts.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class PayWaysModelBean extends ObjectsSelectModelBean
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