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
	public static final long serialVersionUID = 0L;


	/* Goods Model Bean */

	public String[] getSearchGoods()
	{
		return searchGoods;
	}

	private String[] searchGoods;

	public void setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}

	public String getSelSet()
	{
		return selSet;
	}

	private String selSet;

	public void setSelSet(String selSet)
	{
		this.selSet = selSet;
	}


	/* Model Bean Data Access */

	public ModelData modelData()
	{
		return new GoodsModelData(this);
	}
}