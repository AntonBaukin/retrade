package com.tverts.retrade.domain.invoice;

/* Java */

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
	/* public: Volume Data (bean) */

	public List<VolGood> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<VolGood>(4));
	}

	private List<VolGood> goods;

	public void setGoods(List<VolGood> goods)
	{
		this.goods = goods;
	}
}