package com.tverts.retrade.web.data.goods;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.goods.GoodsModelBean;

/* com.tverts: support */

import com.tverts.support.SU;


/**
 * Model data provider to display all
 * the Good Unit instances.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = { "model", "goodsNumber", "goods" })
public class GoodsModelData implements ModelData
{
	/* public: constructors */

	public GoodsModelData()
	{}

	public GoodsModelData(GoodsModelBean model)
	{
		this.model = model;
	}


	/* Goods Model Data */

	@XmlElement
	public GoodsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getGoodsNumber()
	{
		assignGoodsFolder();
		return bean(GetGoods.class).countGoodUnits(getModel());
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	@SuppressWarnings("unchecked")
	public List<GoodUnitView> getGoods()
	{
		assignGoodsFolder();

		List<Object[]>     sel = (List<Object[]>) bean(GetGoods.class).
		  selectGoodUnits(getModel());
		List<GoodUnitView> res = new ArrayList<GoodUnitView>(sel.size());

		for(Object[] o : sel)
			res.add(new GoodUnitView().
			  init(o).initVolume((BigDecimal)o[2])
			);

		return res;
	}

	protected void assignGoodsFolder()
	{
		String p = SU.s2s(request().getParameter("goodsFolder"));
		model.setGoodsFolder((p == null)?(null):Long.parseLong(p));
	}


	/* private: model */

	private GoodsModelBean model;
}