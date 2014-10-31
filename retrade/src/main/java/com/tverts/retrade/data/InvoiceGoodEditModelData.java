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

/* com.tverts: servlet */

import static com.tverts.servlet.RequestPoint.request;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (goods + prices + invoices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.GoodUnitView;
import com.tverts.retrade.domain.prices.PriceListView;
import com.tverts.retrade.domain.invoice.InvoiceGoodEditModelBean;

/* com.tverts: support */

import static com.tverts.support.SU.s2s;


/**
 * COMMENT InvoiceGoodEditModelData
 * TODO support paged goods table when selecting good unit
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goodUnits", "priceLists"})
public class InvoiceGoodEditModelData implements ModelData
{
	/* public: constructors */

	public InvoiceGoodEditModelData()
	{}

	public InvoiceGoodEditModelData(InvoiceGoodEditModelBean model)
	{
		this.model = model;
	}


	/* public: InvoiceModelData (bean) interface */

	@XmlElement
	public InvoiceGoodEditModelBean getModel()
	{
		return model;
	}

	@XmlElement(name = "good-unit")
	@XmlElementWrapper(name = "good-units")
	public List<GoodUnitView> getGoodUnits()
	{
		//?: {request for price lists} skip
		if(request().getParameter("goodUnit") != null)
			return null;

		Domain domain = getModel().invoiceModelBean().findDomain();
		if(domain == null) return null;

		List<GoodUnit>     gus = bean(GetGoods.class).searchGoodUnits(getModel());
		List<GoodUnitView> res = new ArrayList<GoodUnitView>(gus.size());

		for(GoodUnit gu : gus)
			res.add(new GoodUnitView().init(gu));

		return res;
	}

	@XmlElementWrapper(name = "price-lists")
	@XmlElement(name = "price-list")
	@SuppressWarnings("unchecked")
	public List<PriceListView> getPriceLists()
	{
		String gup = s2s(request().getParameter("goodUnit"));
		if(gup == null) return null;
		
		List   sel = bean(GetGoods.class).
		  getGoodPricesLists(Long.parseLong(gup));
		List   res = new ArrayList(sel.size());

		for(Object o : sel)
			res.add(new PriceListView().init(o));

		return res;
	}


	/* private: the model */

	private InvoiceGoodEditModelBean model;
}