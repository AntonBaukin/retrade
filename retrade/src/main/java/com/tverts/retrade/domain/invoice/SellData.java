package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;


/**
 * Data of a Sell Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class SellData extends InvoiceData
{
	/* public: bean interface */

	public List<SellGood> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<SellGood>(0));
	}

	public void setGoods(List<SellGood> goods)
	{
		this.goods = goods;
	}


	/* sell invoice goods */

	private List<SellGood> goods;
}