package com.tverts.retrade.domain.invoice.gen;

/* standard Java class */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.prices.GoodPrice;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.BuyData;
import com.tverts.retrade.domain.invoice.BuyGood;
import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceData;


/**
 * Generates Invoices of Buy type.
 *
 * @author anton.baukin@gmail.com
 */
public class GenInvoiceBuy extends GenInvoiceBase
{
	/* protected: GenInvoiceBase interface */

	protected static final String CTX_GOODS =
	  GenInvoiceBuy.class.getName() + ": selected goods";

	protected GoodUnit[]  selectGoods(GenCtx ctx, InvoiceData data)
	{
		GoodUnit[]          res = (GoodUnit[]) ctx.get(CTX_GOODS);
		if(res != null) return res;

		//~: select goods allowed for buy
		ArrayList<GoodUnit> sel = new ArrayList<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class))
		);

		for(Iterator<GoodUnit> i = sel.iterator();(i.hasNext());)
			if(!Goods.canBuyGood(i.next()))
				i.remove();

		//~: retain only those having prices
		retainGoodsWithPrices(ctx, sel);

		res = sel.toArray(new GoodUnit[sel.size()]);
		ctx.set(CTX_GOODS, res);

		return res;
	}

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		return new BuyData();
	}

	protected InvGood     createGood()
	{
		return new BuyGood();
	}

	protected void        assignGood(GenCtx ctx, InvGood good)
	{
		//~: select good price
		GoodPrice gp = selectGoodPrice(ctx, good);

		//~: {buy invoice} reduce the price to [-35%; -15%]
		BigDecimal p = gp.getPrice().
		  multiply(new BigDecimal("0." + (65 + ctx.gen().nextInt(21)))).
		  setScale(2, BigDecimal.ROUND_HALF_DOWN);

		//~: assign cost
		((BuyGood)good).setCost(
		  good.getVolume().multiply(p).setScale(5)
		);
	}
}