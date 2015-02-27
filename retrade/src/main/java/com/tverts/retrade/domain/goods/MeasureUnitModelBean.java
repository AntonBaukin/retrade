package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ViewModelBeanBase;

/* com.tverts: retrade data */

import com.tverts.retrade.web.data.goods.MeasureUnitModelData;


/**
 * Model to list all the Measure Units
 * of Domain and to create-edit them.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "measure-unit-model")
public class MeasureUnitModelBean extends ViewModelBeanBase
{
	/* View */

	public MeasureUnitView getView()
	{
		return (MeasureUnitView) super.getView();
	}

	public Class viewClass()
	{
		return MeasureUnitView.class;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new MeasureUnitModelData(this);
	}
}