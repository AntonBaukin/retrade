package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: retrade endure (goods) */

import com.tverts.retrade.domain.goods.GetGoods;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.RepriceDocEditModelData;


/**
 * Model bean for {@link RepriceDocEdit} is being edited.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class RepriceDocEditModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: RepriceDocEditModelBean (bean) interface */

	public RepriceDocEdit getRepriceDoc()
	{
		return repriceDoc;
	}

	public void setRepriceDoc(RepriceDocEdit rde)
	{
		this.repriceDoc = rde;
	}


	/* public: RepriceDocEditModelBean (support) interface */

	public RepriceDoc repriceDoc()
	{
		return (getRepriceDoc() == null)?(null):
		  bean(GetGoods.class).getRepriceDoc(
		    getRepriceDoc().getObjectKey());
	}


	/* public: ModelBean (data access) interface */

	public ModelData  modelData()
	{
		return new RepriceDocEditModelData(this);
	}


	/* private: the state of the model */

	private RepriceDocEdit repriceDoc;
}