package com.tverts.retrade.domain.prices;

/* com.tverts: model */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: retrade model data */

import com.tverts.retrade.data.PriceChangeEditModelData;


/**
 * Model bean to store {@link PriceChangeEdit} instance.
 *
 * @author anton.baukin@gmail.com
 */
public class PriceChangeEditModelBean extends ModelBeanBase
{
	/* Price Change Edit Model Bean */

	public PriceChangeEdit getPriceChange()
	{
		return priceChange;
	}

	public void            setPriceChange(PriceChangeEdit priceChange)
	{
		this.priceChange = priceChange;
	}

	public String          getRepriceDocModelKey()
	{
		return repriceDocModelKey;
	}

	public void            setRepriceDocModelKey(String rdmKey)
	{
		this.repriceDocModelKey = rdmKey;
	}

	public String[]        getSearchGoods()
	{
		return searchGoods;
	}

	public void            setSearchGoods(String[] searchGoods)
	{
		this.searchGoods = searchGoods;
	}


	/* Price Change Edit Model Bean (support) */

	public RepriceDocEditModelBean getRepriceDocModel()
	{
		return readModelBean(getRepriceDocModelKey(),
		  RepriceDocEditModelBean.class);
	}


	/* public: ModelBean (data access) interface */

	public ModelData       modelData()
	{
		return new PriceChangeEditModelData(this);
	}


	/* private: the edit instance */

	private PriceChangeEdit priceChange;
	private String          repriceDocModelKey;


	/* private: edit form support */

	private String[]        searchGoods;
}