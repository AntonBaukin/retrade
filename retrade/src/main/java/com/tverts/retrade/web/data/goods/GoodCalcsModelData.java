package com.tverts.retrade.web.data.goods;

/* Java */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodCalcView;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides open time ordered list of the given
 * Good Unit Calculations.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodCalcs"})
public class GoodCalcsModelData implements SimpleModelData
{
	public static final long serialVersionUID = 20150323L;


	/* public: constructors */

	public GoodCalcsModelData()
	{}

	public GoodCalcsModelData(SimpleModelBean model)
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

	@XmlElement(name = "good-calc")
	@XmlElementWrapper(name = "good-calcs")
	public List<GoodCalcView> getGoodCalcs()
	{
		List<GoodCalc>     calcs = bean(GetGoods.class).getGoodCalcs(
		  EX.assertn((Long) getModel().get(GoodUnit.class))
		);
		List<GoodCalcView> res   = new ArrayList<GoodCalcView>(calcs.size());

		for(GoodCalc c : calcs)
			res.add(new GoodCalcView().init(c));

		Collections.sort(calcs, new Comparator<GoodCalc>()
		{
			public int compare(GoodCalc l, GoodCalc r)
			{
				return l.getOpenTime().compareTo(r.getOpenTime());
			}
		});

		return res;
	}


	/* private: model */

	private transient SimpleModelBean model;
}