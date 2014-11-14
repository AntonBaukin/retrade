package com.tverts.retrade.domain.prices;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


/**
 * Edit state of price change document.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "reprice-document-edit")
@XmlType(name = "reprice-document-edit")
public class RepriceDocEdit extends RepriceDocView
{
	public static final long serialVersionUID = 20140803L;


	/* Price Change Document Edit */

	@XmlElement(name = "price-item")
	@XmlElementWrapper(name = "price-items")
	public List<PriceChangeEdit> getPriceChanges()
	{
		return (priceChanges != null)?(priceChanges):
		  (priceChanges = new ArrayList<PriceChangeEdit>());
	}

	private List<PriceChangeEdit> priceChanges;

	public void setPriceChanges(List<PriceChangeEdit> priceChanges)
	{
		this.priceChanges = priceChanges;
	}


	/* Initialization */

	@SuppressWarnings("unchecked")
	public RepriceDocEdit init(RepriceDoc rd)
	{
		List sel = bean(GetPrices.class).selectPriceChanges(rd);

		//~: create price changes (edit)
		for(Object obj : sel)
			getPriceChanges().add(new PriceChangeEdit().init(obj));

		return (RepriceDocEdit)super.init(rd);
	}
}