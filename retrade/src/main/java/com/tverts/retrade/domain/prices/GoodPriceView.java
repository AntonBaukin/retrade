package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnitView;


/**
 * Special version of good info for Price List entries.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-price")
public class GoodPriceView extends GoodUnitView
{
	public static final long serialVersionUID = 0L;

	/* public: get-bean interface */

	@XmlElement
	public Long getGoodPrice()
	{
		return goodPrice;
	}

	@XmlElement
	public Long getPriceList()
	{
		return priceList;
	}

	@XmlElement
	public String getPriceListCode()
	{
		return priceListCode;
	}

	@XmlElement
	public String getPriceListName()
	{
		return priceListName;
	}


	/* public: initialization interface */

	public GoodPriceView init(Object obj)
	{
		if(obj instanceof PriceList)
			this.init((PriceList) obj);

		return (GoodPriceView) super.init(obj);
	}

	public GoodPriceView init(GoodPrice gp)
	{
		this.goodPrice = gp.getPrimaryKey();
		return (GoodPriceView) super.init(gp);
	}

	public GoodPriceView init(PriceList p)
	{
		this.priceList     = p.getPrimaryKey();
		this.priceListCode = p.getCode();
		this.priceListName = p.getName();

		return this;
	}


	/* price list attributes */

	private Long   goodPrice;
	private Long   priceList;
	private String priceListCode;
	private String priceListName;
}