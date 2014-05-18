package com.tverts.retrade.domain.store;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: models */

import com.tverts.model.ModelBeanBase;
import com.tverts.model.ModelData;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade (model data) */

import com.tverts.retrade.data.StoresModelData;


/**
 * Model bean for table with views on all stores
 * of the domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model")
public class StoresModelBean extends ModelBeanBase
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
		return new StoresModelData(this);
	}


	/* store view (fore edit) */

	private CatItemView view;
}