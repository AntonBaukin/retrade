package com.tverts.retrade.domain.invoice.gen;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodCalc;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GoodPrice;

/* com.tverts: retrade domain (invoices) */

import com.tverts.retrade.domain.invoice.InvGood;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.NeedCalcGood;
import com.tverts.retrade.domain.invoice.SellData;
import com.tverts.retrade.domain.invoice.SellGood;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * Generates Invoices of Sell type.
 *
 * @author anton.baukin@gmail.com
 */
public class GenInvoiceSell extends GenInvoiceBase
{
	/* public: GenInvoiceSell (bean) interface */

	@Param
	public int  getPricesPercent()
	{
		return pricesPercent;
	}

	public void setPricesPercent(int p)
	{
		EX.assertx((p >= 0) && (p <= 100));
		this.pricesPercent = p;
	}

	public int getCalcedPercent()
	{
		return calcedPercent;
	}

	@Param
	public void setCalcedPercent(int p)
	{
		EX.assertx((p >= 0) && (p <= 100));
		this.calcedPercent = p;
	}


	/* protected: GenInvoiceBase interface */

	protected static final String CTX_GOODS =
	  GenInvoiceSell.class.getName() + ": selected goods";

	@SuppressWarnings("unchecked")
	protected GoodUnit[]  selectGoods(GenCtx ctx, InvoiceData data)
	{
		GoodUnit[] res = (GoodUnit[]) ctx.get(CTX_GOODS);
		if(res != null) return res;

		ArrayList<GoodUnit> sel = new ArrayList<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class))
		);

		//~: retain only those having prices
		retainGoodsWithPrices(ctx, sel);

		res = sel.toArray(new GoodUnit[sel.size()]);
		ctx.set(CTX_GOODS, res);

		return res;
	}

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		return new SellData();
	}

	protected InvGood     createGood()
	{
		return new SellGood();
	}

	protected void        assignGood(GenCtx ctx, InvGood good)
	{
		//~: select good price
		GoodPrice gp = selectGoodPrice(ctx, good);

		//~: volume cost (volume * price)
		((SellGood)good).setCost(good.getVolume().
		  multiply(gp.getPrice()).setScale(5)
		);

		//~: assign good price
		if(1 + ctx.gen().nextInt(100) <= getPricesPercent())
			((SellGood)good).setPriceList(
			  HiberPoint.reload(gp.getPriceList()));
	}

	protected boolean     hasResGoods(GenCtx ctx, InvoiceData data)
	{
		return true;
	}

	protected boolean     isToCalcGood(GenCtx ctx, InvGood good)
	{
		return (1 + ctx.gen().nextInt(100) <= getCalcedPercent());
	}

	protected void        genResGoods(GenCtx ctx, InvoiceData data)
	{
		GetGoods get  = bean(GetGoods.class);
		Date     date = data.getInvoice().getInvoiceDate();

		//~: search for the products
		boolean found = false;
		for(InvGood g : data.getGoods()) if(g instanceof NeedCalcGood)
		{
			GoodCalc calc = get.getGoodCalc(g.getGoodUnit().getPrimaryKey(), date);

			//?: {the good is not a product | semi-ready}
			if((calc == null) || calc.isSemiReady())
				continue;

			//?: {not to calculate it}
			if((1 + ctx.gen().nextInt(100) > getCalcedPercent()))
				((NeedCalcGood)g).setNeedCalc(false);
			else
				found = true;
		}

		//?: {has produced goods}
		if(found)
		{
			data.setSubType('A');

			//!: regenerate the invoice code
			data.getInvoice().setCode(genInvoiceCode(ctx, data.getInvoice()));
		}

		if(found) LU.I(ctx.log(), "Sell Invoice code: [",
		  data.getInvoice().getCode(), "] is altered!"
		);
	}


	/* private: probabilities */

	private int pricesPercent = 75;
	private int calcedPercent = 75;
}