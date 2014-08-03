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

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;


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


	/* public: RepriceDocEdit (bean) interface */

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


	/* public: initialization interface */

	@SuppressWarnings("unchecked")
	public RepriceDocEdit init(RepriceDoc rd)
	{
		List sel = bean(GetGoods.class).selectPriceChanges(rd);

		//~: create price changes (edit)
		for(Object obj : sel)
			getPriceChanges().add(new PriceChangeEdit().init(obj));

		return (RepriceDocEdit)super.init(rd);
	}
}