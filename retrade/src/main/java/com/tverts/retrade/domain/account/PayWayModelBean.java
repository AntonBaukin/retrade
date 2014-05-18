package com.tverts.retrade.domain.account;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.UnityModelBean;

/* com.tverts: retrade models data */

import com.tverts.retrade.data.account.PayWayModelData;


/**
 * Model bean with main info on a {@link PayWay}.
 *
 * @author anton.baukin@gmail.com
 */
public class PayWayModelBean extends UnityModelBean
{
	public static final long serialVersionUID = 0L;


	/* info interface */

	public PayWay accessEntity()
	{
		return (PayWay) super.accessEntity();
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PayWayModelData(this);
	}
}