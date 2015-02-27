package com.tverts.retrade.web.data;

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

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.GoodPriceHistoryView;
import com.tverts.retrade.domain.prices.GoodPriceModelBean;


/**
 * Model data provider to get the changes of prices
 * for {@link GoodPriceModelBean} model.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "pricesNumber", "goodPrices"})
public class GoodPriceModelData implements ModelData
{
	/* public: constructors */

	public GoodPriceModelData()
	{}

	public GoodPriceModelData(GoodPriceModelBean model)
	{
		this.model = model;
	}


	/* public: GoodPriceModelData (bean) interface */

	@XmlElement
	public GoodPriceModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getPricesNumber()
	{
		return bean(GetPrices.class).
		  countPriceHistory(getModel());
	}

	@XmlElement(name = "price-history")
	@XmlElementWrapper(name = "prices-history")
	@SuppressWarnings("unchecked")
	public List<GoodPriceHistoryView> getGoodPrices()
	{
		List sel = bean(GetPrices.class).
		  selectPriceHistory(getModel());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new GoodPriceHistoryView().init(o));

		return res;
	}


	/* private: the model */

	private GoodPriceModelBean model;
}