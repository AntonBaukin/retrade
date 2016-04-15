package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;


/**
 * Data of a Buy Invoice.
 *
 * @author anton.baukin@gmail.com
 */
public class BuyData extends InvoiceData
{
	/* public: bean interface */

	public List<BuyGood> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<BuyGood>(0));
	}

	public void setGoods(List<BuyGood> goods)
	{
		this.goods = goods;
	}


	/* buy invoice goods */

	private List<BuyGood> goods;
}