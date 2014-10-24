package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ViewModelBeanBase;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListsModelData;


/**
 * Model bean for table with views on all
 * price lists of the domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
@XmlType(name = "price-lists-model")
public class PriceListsModelBean extends ViewModelBeanBase
{
	/* View */

	public CatItemView getView()
	{
		return (CatItemView) super.getView();
	}

	public Class viewClass()
	{
		return CatItemView.class;
	}


	/* Model Bean (data access) */

	public ModelData modelData()
	{
		return new PriceListsModelData(this);
	}
}