package com.tverts.retrade.domain.goods;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.DataSelectModelBean;
import com.tverts.model.ModelData;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.goods.GoodsModelData;


/**
 * Model bean for table with views on all the goods.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "goods-model")
public class GoodsModelBean extends DataSelectModelBean
{
	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new GoodsModelData(this);
	}
}