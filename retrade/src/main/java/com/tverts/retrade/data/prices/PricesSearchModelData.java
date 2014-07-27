package com.tverts.retrade.data.prices;

/* Java */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.GoodPriceView;
import com.tverts.retrade.domain.prices.PriceListEntity;
import com.tverts.retrade.domain.prices.PricesSearchModelBean;


/**
 * Model data provider to search for the Goods,
 * Price Lists, and the Prices.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodPrices"})
public class PricesSearchModelData implements ModelData
{
	/* public: constructors */

	public PricesSearchModelData()
	{}

	public PricesSearchModelData(PricesSearchModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public PricesSearchModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodPriceView> getGoodPrices()
	{
		if(!ModelRequest.isKey("prices"))
			return null;

		//~: get all the price lists
		List<PriceListEntity> lists = bean(GetGoods.class).
		  getPriceLists(getModel().getDomain());

		//~: init the views
		List<GoodPriceView>   res   =
		  new ArrayList<GoodPriceView>(lists.size());
		HashMap<Long, GoodPriceView> views =
		  new HashMap<Long, GoodPriceView>(lists.size());

		for(PriceListEntity pl : lists)
		{
			GoodPriceView v = new GoodPriceView().init(pl);
			res.add(v); views.put(pl.getPrimaryKey(), v);
		}

		//~: insert no-price-list option
		res.add(0, new GoodPriceView());

		//?: {has no good selected yet} no prices
		if(model.getGoodUnit() == null)
			return res;

		//~: assign the prices
		List<GoodPrice> prices = bean(GetGoods.class).
		  getGoodPrices(model.getGoodUnit());

		for(GoodPrice gp : prices)
		{
			GoodPriceView v = views.get(gp.getPriceList().getPrimaryKey());
			if(v != null) v.init(gp);
		}

		return res;
	}


	/* the model */

	private PricesSearchModelBean model;
}