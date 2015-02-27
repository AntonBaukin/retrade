package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;
import com.tverts.model.ViewModelBeanBase;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.web.data.RepriceDocEditModelData;


/**
 * Model bean for {@link RepriceDocEdit} is being edited.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-change-document-edit-model")
public class RepriceDocEditModelBean extends ViewModelBeanBase
{
	/* Price Change Document Edit Model */

	public RepriceDocEdit getView()
	{
		return (RepriceDocEdit) super.getView();
	}

	public Class viewClass()
	{
		return RepriceDocEdit.class;
	}


	/* public: RepriceDocEditModelBean (support) interface */

	public RepriceDoc repriceDoc()
	{
		return (getView() == null)?(null):
		  bean(GetPrices.class).getRepriceDoc(
		    getView().getObjectKey());
	}


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return new RepriceDocEditModelData(this);
	}
}