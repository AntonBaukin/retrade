package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PricesSearchModelData;


/**
 * Model bean to search for the Goods,
 * Price Lists, and the Prices.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class PricesSearchModelBean extends DataSelectModelBean
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public String[] getSearchGoods()
	{
		return searchGoods;
	}

	public void setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}

	public Long getPriceList()
	{
		return priceList;
	}

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(Long goodUnit)
	{
		this.goodUnit = goodUnit;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PricesSearchModelData(this);
	}


	/* private: model attributes */

	private String[] searchGoods;
	private Long     priceList;
	private Long     goodUnit;
}