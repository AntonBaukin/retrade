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

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.RepriceDocView;
import com.tverts.retrade.domain.prices.RepriceDocsModelBean;


/**
 * Data provider for price change documents table.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "repricesNumber", "reprices"})
public class RepriceDocsModelData implements ModelData
{
	/* public: constructors */

	public RepriceDocsModelData()
	{}

	public RepriceDocsModelData(RepriceDocsModelBean model)
	{
		this.model = model;
	}


	/* public: RepriceDocsModelData (data access) interface */

	@XmlElement
	public RepriceDocsModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public long getRepricesNumber()
	{
		return bean(GetPrices.class).countReprices(getModel());
	}

	@XmlElement(name = "reprice-doc")
	@XmlElementWrapper(name = "reprice-docs")
	@SuppressWarnings("unchecked")
	public List<RepriceDocView> getReprices()
	{
		List sel = bean(GetPrices.class).
		  selectReprices(getModel());
		List res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new RepriceDocView().init(o));

		return res;
	}


	/* private: model */

	private RepriceDocsModelBean model;
}