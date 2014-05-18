package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement(name = "model")
public class GoodsModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: GoodsModelBean (bean) interface */

	public String[] getSearchGoods()
	{
		return searchGoods;
	}

	public void     setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}

	public String   getSelSet()
	{
		return selSet;
	}

	public void     setSelSet(String selSet)
	{
		this.selSet = selSet;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new GoodsModelData(this);
	}


	/* private: model attributes */

	private String[] searchGoods;
	private String   selSet;
}