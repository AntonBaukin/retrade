package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides data for table with price changes history.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-history")
@XmlType(name = "price-history-view")
public class GoodPriceHistoryView extends PriceChangeView
{
	public static final long serialVersionUID = 20140803L;


	/* Good Price History View */

	public String getRepriceKey()
	{
		return repriceKey;
	}

	private String repriceKey;

	public void setRepriceKey(String repriceKey)
	{
		this.repriceKey = repriceKey;
	}

	public String getRepriceName()
	{
		return repriceName;
	}

	private String repriceName;

	public void setRepriceName(String repriceName)
	{
		this.repriceName = repriceName;
	}


	/* Initialization */

	public GoodPriceHistoryView init(Object obj)
	{
		if(obj instanceof RepriceDoc)
			return this.init((RepriceDoc)obj);

		return (GoodPriceHistoryView)super.init(obj);
	}

	public GoodPriceHistoryView init(RepriceDoc rd)
	{
		repriceKey  = rd.getPrimaryKey().toString();
		repriceName = rd.getCode();

		return this;
	}
}