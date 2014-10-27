package com.tverts.retrade.domain.goods;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.goods.GoodsModelData;


/**
 * Model bean for table with views on all the goods.
 *
 * @author anton.baukin@gmail.com
 */
public class GoodsModelBean extends DataSelectModelBean
{
	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new GoodsModelData(this);
	}
}