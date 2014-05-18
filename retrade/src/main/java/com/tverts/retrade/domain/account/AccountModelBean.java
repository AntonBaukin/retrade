package com.tverts.retrade.domain.account;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade models data */

import com.tverts.retrade.data.account.AccountModelData;


/**
 * Model bean with main info on an {@link Account}.
 *
 * @author anton.baukin@gmail.com
 */
public class AccountModelBean extends UnityModelBean
{
	public static final long serialVersionUID = 0L;


	/* info interface */

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