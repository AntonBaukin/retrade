package com.tverts.retrade.domain.invoice.gen;

/* Java */

import java.math.BigDecimal;
import java.util.List;

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

	protected boolean     isGoodAllowed(GoodUnit gu)
	{
		return Goods.canBuyGood(gu);
	}

	protected void        filterGoods(GenCtx ctx, List<GoodUnit> goods)
	{
		super.filterGoods(ctx, goods);

		//~: retain only those having prices
		retainGoodsWithPrices(ctx, goods);
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
		((BuyGood)good).setCost(good.getVolume().multiply(p).setScale(5));
	}
}