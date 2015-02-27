package com.tverts.retrade.web.data;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: endure (catalogues) */

import com.tverts.endure.cats.CatItemView;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.StoresModelBean;


/**
 * Model data provider to display all
 * the Trade Store instances of the domain.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "stores"})
public class StoresModelData implements ModelData
{
	/* public: constructors */

	public StoresModelData()
	{}

	public StoresModelData(StoresModelBean model)
	{
		this.model = model;
	}


	/* public: GoodsModelData (bean) interface */

	@XmlElement
	public StoresModelBean getModel()
	{
		return model;
	}

	@XmlElementWrapper(name = "stores")
	@XmlElement(name = "store")
	@SuppressWarnings("unchecked")
	public List<CatItemView> getStores()
	{
		List sel = bean(GetTradeStore.class).
		  getTradeStores(getModel().domain());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new CatItemView().init(o));

		return res;
	}


	/* private: model */

	private StoresModelBean model;
}