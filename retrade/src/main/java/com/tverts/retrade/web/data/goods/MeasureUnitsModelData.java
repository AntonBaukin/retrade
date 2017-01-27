package com.tverts.retrade.web.data.goods;

/* Java */

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

import com.tverts.model.SimpleModelBean;
import com.tverts.model.SimpleModelData;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.MeasureUnit;
import com.tverts.retrade.domain.goods.MeasureUnitView;


/**
 * Lists Measures of the Domain of the model.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "measureUnits"})
public class MeasureUnitsModelData implements SimpleModelData
{
	public static final long serialVersionUID = 20150223L;


	/* public: constructors */

	public MeasureUnitsModelData()
	{}

	public MeasureUnitsModelData(SimpleModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	public SimpleModelBean getModel()
	{
		return model;
	}

	public void setModel(SimpleModelBean model)
	{
		this.model = model;
	}

	@XmlElement(name = "measure-unit")
	@XmlElementWrapper(name = "measure-units")
	public List<MeasureUnitView> getMeasureUnits()
	{
		List<MeasureUnit> mus = bean(GetGoods.class).
		  getMeasureUnits(getModel().domain());

		List<MeasureUnitView> res = new ArrayList<>(mus.size());

		for(MeasureUnit mu : mus)
			res.add(new MeasureUnitView().init(mu));

		return res;
	}


	/* private: model */

	private transient SimpleModelBean model;
}