package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.prices.PriceListsModelData;


/**
 * Model bean for table with views on all
 * price lists of the domain.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class PriceListsModelBean extends ModelBeanBase
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public CatItemView getView()
	{
		return view;
	}

	public void setView(CatItemView view)
	{
		this.view = view;
	}


	/* public: ModelBean (data access) interface */

	public ModelData modelData()
	{
		return new PriceListsModelData(this);
	}


	/* price list view (fore edit) */

	private CatItemView view;
}