package com.tverts.api.retrade.prices;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;


/**
 * A Price List position referring the List.
 * Used when the Price List is not clear
 * from the context as for sell operations.
 */
@XmlType(name = "price-item",
  propOrder = { "list", "XList" }
)
public class PriceItem extends GoodPrice
{
	/**
	 * The primary key of the price list that
	 * is assigned when the good price is selected
	 * based on that price list.
	 */
	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "price-list")
	public Long getList()
	{
		return (list == 0L)?(null):(list);
	}

	private long list;

	public void setList(Long list)
	{
		this.list = (list == null)?(0L):(list);
	}

	@XmlElement(name = "xprice-list")
	public String getXList()
	{
		return xlist;
	}

	private String xlist;

	public void setXList(String xlist)
	{
		this.xlist = xlist;
	}
}