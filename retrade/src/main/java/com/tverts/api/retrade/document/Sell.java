package com.tverts.api.retrade.document;

/* standard Java classes */

import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.retrade.goods.GoodSell;


/**
 * Defines Sell Invoice.
 */
@XmlType(name = "sell")
public class Sell extends BuySell
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<GoodSell> getGoods()
	{
		return super.getGoods();
	}
}