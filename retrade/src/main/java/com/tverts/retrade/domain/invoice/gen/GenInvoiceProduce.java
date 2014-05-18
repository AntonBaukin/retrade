package com.tverts.retrade.domain.invoice.gen;

/* standard Java class */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;

/* com.tverts: retrade domain (goods + invoices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.InvoiceData;
import com.tverts.retrade.domain.invoice.Invoices;
import com.tverts.retrade.domain.invoice.MoveData;
import com.tverts.retrade.domain.invoice.MoveGood;
import com.tverts.retrade.domain.invoice.ResGood;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Generates Production Invoices being special
 * sub-types of Move Invoices.
 *
 * @author anton.baukin@gmail.com
 */
public class GenInvoiceProduce extends GenInvoiceMove
{
	/* public: bean interface */

	public int  getAutoPercent()
	{
		return autoPercent;
	}

	@Param
	public void setAutoPercent(int p)
	{
		EX.assertx((p >= 0) && (p <= 100));
		this.autoPercent = p;
	}


	/* protected: GenInvoiceBase interface */

	protected UnityType   getDayMarkType()
	{
		return Invoices.typeInvoiceFreeProduce();
	}

	protected InvoiceData createInvoiceData(GenCtx ctx)
	{
		MoveData d = new MoveData();

		//?: {it is auto-production}
		if((1 + ctx.gen().nextInt(100) <= getAutoPercent()))
			d.setSubType('A');
		//~: it is free production
		else
			d.setSubType('P');

		return d;
	}

	/**
	 * All altered Move Invoices has the source Store
	 * the same the destination one.
	 */
	protected void        assignSourceStore(GenCtx ctx, InvoiceData data)
	{
		((MoveData)data).setSourceStore(data.getStore());
	}

	protected static final String CTX_GOODS_WITH_CALCS =
	  GenInvoiceProduce.class.getName() + ": goods with calculations";

	protected GoodUnit[]  selectGoods(GenCtx ctx, InvoiceData data)
	{
		GoodUnit[] res = ctx.get(GoodUnit[].class);

		//?: {it is free production} select any good
		if(Invoices.isFreeProduceInvoice(data))
			return res;

		res = (GoodUnit[])ctx.get(CTX_GOODS_WITH_CALCS);
		if(res != null) return res;

		//~: retain goods having calculations
		Set<GoodUnit> set = new LinkedHashSet<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class))
		);

		for(Iterator<GoodUnit> i = set.iterator();(i.hasNext());)
			if(i.next().getGoodCalc() == null) i.remove();

		res = set.toArray(new GoodUnit[set.size()]);
		EX.asserte(res, "Domain has no Goods with Calculations!");
		ctx.set(CTX_GOODS_WITH_CALCS, res);

		return res;
	}

	protected void        genResGoods(GenCtx ctx, InvoiceData data)
	{
		//?: {it is free production}
		if(Invoices.isFreeProduceInvoice(data))
			genFreeProduction(ctx, (MoveData)data);
	}

	/**
	 * For free production we just add some of the goods
	 * to the resulting list.
	 */
	protected void        genFreeProduction(GenCtx ctx, MoveData d)
	{
		//~: take all the goods
		Set<GoodUnit> gs = new HashSet<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class))
		);

		//~: remove goods present (produced)
		for(MoveGood g : d.getGoods())
			gs.remove(g.getGoodUnit());

		//?: {nothing left} take all the goods
		if(gs.isEmpty())
			gs.addAll(Arrays.asList(ctx.get(GoodUnit[].class)));

		//~: list of the goods to select
		List<GoodUnit> goods = new ArrayList<GoodUnit>(gs);
		Collections.shuffle(goods, ctx.gen());

		//~: the number of goods (used in 'production')
		int size = d.getGoods().size();
		size    += ctx.gen().nextInt(1 + size/2);
		if(size > gs.size()) size = gs.size();
		size     = size/2 + ctx.gen().nextInt(1 + size/2);
		goods    = goods.subList(0, (size == 0)?(1):(size));

		//~: set move-op for the goods produced
		for(MoveGood g : d.getGoods())
			g.setMoveOn(true); //<-- place-only

		//~: generate the place-only goods
		for(GoodUnit gu : goods)
		{
			MoveGood rg = new MoveGood();
			d.getGoods().add(rg);

			//~: good unit
			rg.setGoodUnit(gu);

			//~: generate volume
			rg.setVolume(createGoodVolume(ctx, rg));

			//!: set move-op flag
			rg.setMoveOn(false); //<-- take-only
		}
	}


	/* generator parameters */

	private int autoPercent = 75;
}