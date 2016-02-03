package com.tverts.retrade.web.data.prices;

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
import com.tverts.retrade.domain.prices.GoodPriceView;
import com.tverts.retrade.domain.prices.PriceListModelBean;


/**
 * COMMENT PriceListModelData
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodsNumber", "goods"})
public class PriceListModelData implements ModelData
{
	/* public: constructors */

	public PriceListModelData()
	{}

	public PriceListModelData(PriceListModelBean model)
	{
		this.model = model;
	}


	/* public: PriceListModelData (bean) interface */

	@XmlElement
	public PriceListModelBean  getModel()
	{
		return model;
	}

	@XmlElement
	public long                getGoodsNumber()
	{
		return bean(GetGoods.class).
		  countGoodPrices(getModel());
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodPriceView> getGoods()
	{
		List sel = bean(GetGoods.class).
		  selectGoodPrices(getModel());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new GoodPriceView().init(o).initAttrs(o));

		return res;
	}


	/* private: the model */

	private PriceListModelBean model;
}