package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListsTreeModelData;


/**
 * Model bean for table with views on Price Lists
 * tree and goods prices in separated table.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "model")
public class PriceListsTreeModelBean extends GoodsModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getCurrentList()
	{
		return currentList;
	}

	public void setCurrentList(Long currentList)
	{
		this.currentList = currentList;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PriceListsTreeModelData(this);
	}


	/* model attribute */

	private Long currentList;
}
