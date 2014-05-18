package com.tverts.retrade.data;

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

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.prices.PriceChangeEditModelBean;


/**
 * Data provider for model bean {@link PriceChangeEditModelBean}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "prices"})
public class PriceChangeEditModelData implements ModelData
{
	/* public: constructors */

	public PriceChangeEditModelData()
	{}

	public PriceChangeEditModelData(PriceChangeEditModelBean model)
	{
		this.model = model;
	}


	/* public: PriceChangeEditModelData interface */

	@XmlElement
	public PriceChangeEditModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "good-price")
	@XmlElementWrapper(name = "good-prices")
	@SuppressWarnings("unchecked")
	public List<GoodUnitView> getPrices()
	{
		List sel = bean(GetGoods.class).
		  selectGoodPrices(getModel(), 25);

		List res = new ArrayList(sel.size());

		for(Object obj : sel)
			res.add(new GoodUnitView().init(obj));
		return res;
	}


	/* private: the model */

	private PriceChangeEditModelBean model;
}