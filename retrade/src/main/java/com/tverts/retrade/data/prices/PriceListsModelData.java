package com.tverts.retrade.data.prices;

/* standard Java classes */

import java.util.ArrayList;
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

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.PriceListView;
import com.tverts.retrade.domain.prices.PriceListsModelBean;


/**
 * Model data provider to display all
 * the Price List instances of the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "priceLists"})
public class PriceListsModelData implements ModelData
{
	/* public: constructors */

	public PriceListsModelData()
	{}

	public PriceListsModelData(PriceListsModelBean model)
	{
		this.model = model;
	}


	/* public: GoodsModelData (bean) interface */

	@XmlElement
	public PriceListsModelBean getModel()
	{
		return model;
	}

	@XmlElementWrapper(name = "price-lists")
	@XmlElement(name = "price-list")
	@SuppressWarnings("unchecked")
	public List<PriceListView> getPriceLists()
	{
		List sel = bean(GetGoods.class).
		  getPriceLists(getModel().domain());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new PriceListView().init(o));

		return res;
	}


	/* private: model */

	private PriceListsModelBean model;
}