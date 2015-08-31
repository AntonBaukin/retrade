package com.tverts.retrade.domain.invoice;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.TradeStore;


/**
 * Data of a Move Invoice.
 *
 * Move Invoice transfers goods from the
 * source store {@link #getSourceStore()}
 * to the destination store {@link #getStore()}
 * where the goods are now stored.
 *
 * Invoices of various sub-types are implemented
 * as altered Move Invoices:
 *
 *  A  auto-produce;
 *  F  free-produce;
 *  P  free-produce prototype;
 *  C  (volume) correction.
 *
 * @author anton.baukin@gmail.com
 */
public class MoveData extends InvoiceData
{
	/* public: Move Data (bean) */

	public TradeStore getSourceStore()
	{
		return sourceStore;
	}

	private TradeStore sourceStore;

	public void setSourceStore(TradeStore sourceStore)
	{
		this.sourceStore = sourceStore;
	}

	public List<MoveGood> getGoods()
	{
		return (goods != null)?(goods):
		  (goods = new ArrayList<MoveGood>(4));
	}

	private List<MoveGood> goods;

	public void setGoods(List<MoveGood> goods)
	{
		this.goods = goods;
	}
}