package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;


/**
 * Edit state of price change document.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class RepriceDocEdit extends RepriceDocView
{
	public static final long serialVersionUID = 0L;


	/* public: RepriceDocEdit (bean) interface */

	public Long getPriceListKey()
	{
		return priceListKey;
	}

	public void setPriceListKey(Long priceListKey)
	{
		this.priceListKey = priceListKey;
	}

	public List<PriceChangeEdit> getPriceChanges()
	{
		return (priceChanges != null)?(priceChanges):
		  (priceChanges = new ArrayList<PriceChangeEdit>());
	}

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

	public RepriceDocEdit init(PriceList pl)
	{
		//~: price list primary key
		priceListKey = pl.getPrimaryKey();

		return (RepriceDocEdit)super.init(pl);
	}


	/* private: price change document attributes */

	private Long                   priceListKey;
	private List<PriceChangeEdit>  priceChanges;
}