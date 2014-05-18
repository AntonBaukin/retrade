package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ModelBeanBase;

/* com.tverts: retrade data */

import com.tverts.retrade.data.goods.MeasureUnitModelData;


/**
 * Model to list all the Measure Units
 * of Domain and to create-edit them.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class MeasureUnitModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public MeasureUnitView getView()
	{
		return view;
	}

	public void setView(MeasureUnitView view)
	{
		this.view = view;
	}


	/* public: ModelBean interface */

	public ModelData modelData()
	{
		return new MeasureUnitModelData(this);
	}


	/* measure unit view */

	private MeasureUnitView view;
}