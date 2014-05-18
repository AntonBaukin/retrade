package com.tverts.retrade.data.goods;

/* standard Java classes */

import java.io.Serializable;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.SimpleModelBean;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.CalcPartView;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodCalcView;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Provides list of the
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"calcParts"})
public class      GoodCalcModelData
       implements Serializable, ModelData
{
	public static final long serialVersionUID = 0L;


	/* public: constructors */

	public GoodCalcModelData()
	{}

	public GoodCalcModelData(SimpleModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	public SimpleModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "calc-part")
	@XmlElementWrapper(name = "calc-parts")
	public List<CalcPartView> getCalcParts()
	{
		//?: {calculation view is provided}
		if(model.get(GoodCalcView.class) != null)
			return ((GoodCalcView) model.get(GoodCalcView.class)).getParts();

		//?: {calculation entity is provided}
		if(model.get(GoodCalc.class) instanceof Long)
		{
			GoodCalc c = EX.assertn(
			  bean(GetGoods.class).getGoodCalc((Long) model.get(GoodCalc.class)),
			  "Good Calculation [", model.get(GoodCalc.class), "] was not found!"
			);

			return new GoodCalcView().initParts(c).getParts();
		}

		//?: {good unit entity is provided}
		if(model.get(GoodUnit.class) instanceof Long)
		{
			GoodUnit gu = EX.assertn(
			  bean(GetGoods.class).getGoodUnit((Long)model.get(GoodUnit.class)),
			  "Good Unit [", model.get(GoodUnit.class), "] was not found!"
			);

			EX.assertn(gu.getGoodCalc(),
			  "Good Unit [", gu.getPrimaryKey(), "] has no Calculation!"
			);

			return new GoodCalcView().initParts(gu.getGoodCalc()).getParts();
		}

		throw EX.state("Calculation Parts are not provided!");
	}


	/* private: model */

	private SimpleModelBean model;
}