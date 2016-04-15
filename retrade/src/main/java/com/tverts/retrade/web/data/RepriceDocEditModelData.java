package com.tverts.retrade.web.data;

/* standard Java classes */

import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: model */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.prices.PriceChangeEdit;
import com.tverts.retrade.domain.prices.RepriceDocEditModelBean;


/**
 * Data provider for Price Change Document create-edit model.
 * Note that all the price changes are shown in the table
 * without paging in a huge list.
 *
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "changes"})
public class RepriceDocEditModelData implements ModelData
{
	/* public: constructors */

	public RepriceDocEditModelData()
	{}

	public RepriceDocEditModelData(RepriceDocEditModelBean model)
	{
		this.model = model;
	}


	/* public: RepriceDocModelData (data access) interface */

	@XmlElement
	public RepriceDocEditModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "price-change")
	@XmlElementWrapper(name = "price-changes")
	@SuppressWarnings("unchecked")
	public List<PriceChangeEdit> getChanges()
	{
		return getModel().getView().getPriceChanges();
	}


	/* private: model */

	private RepriceDocEditModelBean model;
}