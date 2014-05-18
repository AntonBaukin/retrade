package com.tverts.retrade.domain.prices;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Provides data for table with price changes history.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-history")
public class GoodPriceHistoryView extends PriceChangeView
{
	public static final long serialVersionUID = 0L;


	/* public: GoodPriceHistoryView (bean) interface */

	public String      getRepriceKey()
	{
		return repriceKey;
	}

	public void        setRepriceKey(String repriceKey)
	{
		this.repriceKey = repriceKey;
	}

	public String      getRepriceName()
	{
		return repriceName;
	}

	public void        setRepriceName(String repriceName)
	{
		this.repriceName = repriceName;
	}


	/* public: initialization interface */

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


	/* private: properties of the view */

	private String repriceKey;
	private String repriceName;
}