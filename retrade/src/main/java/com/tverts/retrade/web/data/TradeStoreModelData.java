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

/* com.tverts: retrade domain (goods + trade stores) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.store.TradeStoreModelBean;


/**
 * COMMENT TradeStoreModelData
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodsNumber", "goods"})
public class TradeStoreModelData implements ModelData
{
	/* public: constructors */

	public TradeStoreModelData()
	{}

	public TradeStoreModelData(TradeStoreModelBean model)
	{
		this.model = model;
	}


	/* public: TradeStoreModelData (bean) interface */

	@XmlElement
	public TradeStoreModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int                 getGoodsNumber()
	{
		return bean(GetGoods.class).
		  countGoodUnits(getModel());
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodUnitView>  getGoods()
	{
		List sel = bean(GetGoods.class).
		  selectGoodUnits(getModel());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new GoodUnitView().init(o).initAttrs(o));

		return res;
	}


	/* private: the model */

	private TradeStoreModelBean model;
}