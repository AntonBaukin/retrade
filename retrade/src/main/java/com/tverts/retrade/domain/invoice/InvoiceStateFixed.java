package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.StoreGood;


/**
 * A state of {@link Invoice} instance.
 *
 * In this state the invoice has the list of
 * {@link StoreGood}s, but the store owns them.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class InvoiceStateFixed extends InvoiceState
{
	/* public: InvoiceStateFixed bean interface */

	public List<StoreGood> getStoreGoods()
	{
		return (storeGoods != null)?(storeGoods):
		  (storeGoods = new ArrayList<StoreGood>(8));
	}

	public void setStoreGoods(List<StoreGood> storeGoods)
	{
		this.storeGoods = storeGoods;
	}


	/* persisted attributes: the list of invoice' goods in store */

	private List<StoreGood>  storeGoods;
}