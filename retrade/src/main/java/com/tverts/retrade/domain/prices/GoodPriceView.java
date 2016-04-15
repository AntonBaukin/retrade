package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnitView;


/**
 * Special version of good info for Price List entries.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-price")
@XmlType(name = "good-price-view")
public class GoodPriceView extends GoodUnitView
{
	public static final long serialVersionUID = 20140806L;


	/* Good Price View */

	public Long getGoodPrice()
	{
		return goodPrice;
	}

	private Long goodPrice;

	public void setGoodPrice(Long goodPrice)
	{
		this.goodPrice = goodPrice;
	}

	public Long getPriceList()
	{
		return priceList;
	}

	private Long priceList;

	public void setPriceList(Long priceList)
	{
		this.priceList = priceList;
	}

	public String getPriceListCode()
	{
		return priceListCode;
	}

	private String priceListCode;

	public void setPriceListCode(String priceListCode)
	{
		this.priceListCode = priceListCode;
	}

	public String getPriceListName()
	{
		return priceListName;
	}

	private String priceListName;

	public void setPriceListName(String priceListName)
	{
		this.priceListName = priceListName;
	}


	/* Initialization */

	public GoodPriceView init(Object obj)
	{
		if(obj instanceof PriceListEntity)
			this.init((PriceListEntity) obj);

		return (GoodPriceView) super.init(obj);
	}

	public GoodPriceView init(GoodPrice gp)
	{
		this.goodPrice = gp.getPrimaryKey();
		return (GoodPriceView) super.init(gp);
	}

	public GoodPriceView init(PriceListEntity p)
	{
		this.priceList     = p.getPrimaryKey();
		this.priceListCode = p.getCode();
		this.priceListName = p.getName();

		return this;
	}
}