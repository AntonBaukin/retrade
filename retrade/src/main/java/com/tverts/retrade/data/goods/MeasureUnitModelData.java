package com.tverts.retrade.data.goods;

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

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.MeasureUnitView;
import com.tverts.retrade.domain.goods.MeasureUnitModelBean;


/**
 * Model data provider to display all
 * the Good Unit instances.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "measures"})
public class MeasureUnitModelData implements ModelData
{
	/* public: constructors */

	public MeasureUnitModelData()
	{}

	public MeasureUnitModelData(MeasureUnitModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public MeasureUnitModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "measure-unit")
	@XmlElementWrapper(name = "measure-units")
	@SuppressWarnings("unchecked")
	public List<MeasureUnitView> getMeasures()
	{
		List sel = bean(GetGoods.class).
		  getMeasureUnits(model.domain());

		List res = new ArrayList(sel.size());
		for(Object o : sel)
			res.add(new MeasureUnitView().init(o));

		return res;
	}


	/* private: model */

	private MeasureUnitModelBean model;
}