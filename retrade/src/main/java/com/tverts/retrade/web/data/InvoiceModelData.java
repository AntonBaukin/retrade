package com.tverts.retrade.web.data;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: models */

import com.tverts.model.ModelData;
import com.tverts.model.ModelRequest;

/* com.tverts: retrade domain (invoice) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.Invoice;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.InvoiceGoodView;
import com.tverts.retrade.domain.invoice.InvoiceModelBean;


/**
 * Model data to list the goods of an Invoice.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "goods", "results"})
public class InvoiceModelData implements ModelData
{
	/* public: constructors */

	public InvoiceModelData()
	{}

	public InvoiceModelData(InvoiceModelBean model)
	{
		this.model = model;
	}


	/* public: InvoiceModelData (bean) interface */

	@XmlElement
	public InvoiceModelBean      getModel()
	{
		return model;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	@SuppressWarnings("unchecked")
	public List<InvoiceGoodView> getGoods()
	{
		if(!ModelRequest.isKey(""))
			return null;

		Invoice i = getModel().invoice();
		int     n = i.getInvoiceData().getGoods().size();
		List    r = new ArrayList<InvoiceGoodView>(n);

		for(int k = 0;(k < n);k++)
			r.add(new InvoiceGoodView().init(i, k));

		return (List<InvoiceGoodView>)r;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "results")
	@SuppressWarnings("unchecked")
	public List<InvoiceGoodView> getResults()
	{
		if(!ModelRequest.isKey("results"))
			return null;

		InvoiceData d = getModel().invoice().getInvoiceData();
		if(!d.isAltered()) return null;
		List        r = new ArrayList<InvoiceGoodView>(d.getResGoods().size());

		int i = 0, j = 0;
		for(InvGood g : d.getResGoods())
		{
			InvoiceGoodView x = new InvoiceGoodView().init(g);

			if(x.getCalcKey() != null)
				x.init(++i - 1);
			else
				x.init(++j - 1);

			r.add(x);
		}

		return (List<InvoiceGoodView>)r;
	}


	/* private: the model */

	private InvoiceModelBean model;
}