package com.tverts.retrade.data;

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

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.PriceChangeView;
import com.tverts.retrade.domain.prices.RepriceDocModelBean;


/**
 * Data provider for price change document info.
 * Provides all the items of the price change.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "changes"})
public class RepriceDocModelData implements ModelData
{
	/* public: constructors */

	public RepriceDocModelData()
	{}

	public RepriceDocModelData(RepriceDocModelBean model)
	{
		this.model = model;
	}


	/* public: RepriceDocModelData (data access) interface */

	@XmlElement
	public RepriceDocModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "price-change")
	@XmlElementWrapper(name = "price-changes")
	@SuppressWarnings("unchecked")
	public List<PriceChangeView> getChanges()
	{
		List sel = bean(GetPrices.class).
		  selectPriceChanges(getModel());

		List res = new ArrayList(sel.size());

		for(Object obj : sel)
			res.add(new PriceChangeView().init(obj));
		return res;
	}


	/* private: model */

	private RepriceDocModelBean model;
}