package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;


/**
 * Data of a plain volume Invoice such as
 * Volume Check Document.
 *
 * @author anton.baukin@gmail.com
 */
public class VolData extends InvoiceData
{
	/* public: bean interface */

	public List<VolGood> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<VolGood>(0));
	}

	public void setGoods(List<VolGood> goods)
	{
		this.goods = goods;
	}


	/* volume invoice goods */

	private List<VolGood> goods;
}