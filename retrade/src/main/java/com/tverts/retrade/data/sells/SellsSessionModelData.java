package com.tverts.retrade.data.sells;

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

/* com.tverts: models */

import com.tverts.model.ModelData;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.SellReceiptView;
import com.tverts.retrade.domain.sells.SellsSessionModelBean;


/**
 * Model data to list the goods of an Invoice.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "model-data")
@XmlType(propOrder = {"model", "receiptsNumber", "receipts"})
public class SellsSessionModelData implements ModelData
{
	/* public: constructors */

	public SellsSessionModelData()
	{}

	public SellsSessionModelData(SellsSessionModelBean model)
	{
		this.model = model;
	}


	/* public: bean interface */

	@XmlElement
	public SellsSessionModelBean getModel()
	{
		return model;
	}

	@XmlElement
	public int getReceiptsNumber()
	{
		return bean(GetSells.class).
		  countSellReceipts(getModel());
	}

	@XmlElement(name = "receipt")
	@XmlElementWrapper(name = "receipts")
	@SuppressWarnings("unchecked")
	public List<SellReceiptView> getReceipts()
	{
		List rows = bean(GetSells.class).selectSellReceipts(getModel());
		List res  = new ArrayList(rows.size());

		for(int i = 0;(i < rows.size());i++)
			res.add(new SellReceiptView().
			  init(rows.get(i)).
			  initIndex(getModel().getDataStart() + i)
			);

		return (List<SellReceiptView>) res;
	}


	/* the model */

	private SellsSessionModelBean model;
}
