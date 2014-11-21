package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import com.tverts.actions.ActionsPoint;
import com.tverts.actions.ActionType;

/* com.tverts: genesis */

import com.tverts.genesis.DaysGenDisp;
import com.tverts.genesis.DaysGenPart;
import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityTypes;
import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (goods + invoices) */

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.DU;


/**
 * Generates one instance of {@link RepriceDoc}.
 * Supports ONLY {@link DaysGenDisp} generators.
 *
 * @author anton.baukin@gmail.com
 */
public class      GenReprice
       extends    GenesisHiberPartBase
       implements DaysGenPart
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		RepriceDoc rd = new RepriceDoc();

		//=: set test primary key
		setPrimaryKey(ctx.session(), rd, true);

		//=: domain
		rd.setDomain(ctx.get(Domain.class));

		//~: initialize the document
		initRepriceDoc(ctx, rd);

		//~: generate price changes
		genPriceChanges(ctx, rd);

		//!: save the document
		rd.updateOx();
		ActionsPoint.actionRun(ActionType.SAVE, rd,
		  ActionsPoint.UNITY_TYPE, Prices.TYPE_REPRICE_DOC
		);

		//!: fix the prices now
		ActionsPoint.actionRun(Prices.ACT_FIX_PRICES, rd,
		  Prices.CHANGE_TIME, ctx.get(DaysGenDisp.TIME, Date.class)
		);
	}


	/* Generate Price Change Document */

	@Param
	public Integer getMinGoods()
	{
		return minGoods;
	}

	private Integer minGoods;

	public void setMinGoods(Integer n)
	{
		if((n != null) && (n < 1)) throw EX.arg();
		this.minGoods = n;
	}

	@Param
	public Integer getMaxGoods()
	{
		return maxGoods;
	}

	private Integer maxGoods;

	public void setMaxGoods(Integer n)
	{
		if((n != null) && (n < 1)) throw EX.arg();
		this.maxGoods = n;
	}

	@Param
	public int getCostsDelta()
	{
		return costsDelta;
	}

	private int costsDelta = 10;

	public void setCostsDelta(int n)
	{
		EX.assertx((n > 0) & (n < 100));
		this.costsDelta = n;
	}


	/* public: DaysGenPart interface */

	public boolean isDayClear(GenCtx ctx)
	{
		return super.isGenDispDayClear(ctx,
		  UnityTypes.unityType(RepriceDoc.class, Prices.TYPE_REPRICE_DOC)
		);
	}

	public void    markDayGenerated(GenCtx ctx)
	{
		super.markGenDispDay(ctx,
		  UnityTypes.unityType(RepriceDoc.class, Prices.TYPE_REPRICE_DOC)
		);
	}


	/* protected: price changes generation */

	protected void initRepriceDoc(GenCtx ctx, RepriceDoc rd)
	{
		//=: generate document code
		rd.setCode(Prices.createRepriceDocCode(
		  ctx.get(DaysGenDisp.DAY, Date.class),
		  ctx.get(DaysGenDisp.DAYI, Integer.class)
		));

		//~: select random price list
		PriceListEntity[] pls = ctx.get(PriceListEntity[].class);
		rd.setPriceList(pls[ctx.gen().nextInt(pls.length)]);

		//~: change reason
		rd.getOx().setRemarks(String.format(
		  "Тестовая генерация, день %s.",
		  DU.date2str(ctx.get(DaysGenDisp.DAY, Date.class))
		));
	}

	protected void genPriceChanges(GenCtx ctx, RepriceDoc rd)
	{
		GetPrices get = bean(GetPrices.class);

		EX.assertx(rd.getChanges().isEmpty());
		for(GoodPrice gp : genSelectGoods(ctx, rd))
		{
			//~: price change
			PriceChange pc = new PriceChange();
			rd.getChanges().add(pc);

			//=: price change document
			pc.setRepriceDoc(rd);

			//=: good unit
			pc.setGoodUnit(gp.getGoodUnit());

			//~: variate the cost value
			int        d = 100 - costsDelta + ctx.gen().nextInt(2*costsDelta + 1);
			BigDecimal c = gp.getPrice().multiply(new BigDecimal(d)).scaleByPowerOfTen(-2);

			//?: {scale the cost to .XY}
			if(c.scale() != 2)
				c = c.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			//=: the new price
			pc.setPriceNew(c);
		}
	}

	protected List<GoodPrice> genSelectGoods(GenCtx ctx, RepriceDoc rd)
	{
		//HINT: main price list always contains the prices of all the
		// goods to sell, and no good position may be removed or added!

		//~: the target price list
		PriceListEntity pl = EX.assertn(rd.getPriceList());

//		//~: the main price list
//		PriceListEntity mn = EX.assertn(
//		  ctx.get(PriceListEntity.class),
//		  "No Main Price List is found (generated)!"
//		);
//
//		//~: all the goods from the main price list
//		List<GoodPrice> ps = bean(GetPrices.class).getPriceListPrices(mn);
//		EX.asserte(ps, "Main Price List has no prices generated!");

		//TODO: implement add and remove operations of Price List price changes

		List<GoodPrice> ps = bean(GetPrices.class).getPriceListPrices(pl);

		//~: shuffle them
		Collections.shuffle(ps, ctx.gen());

		//~: generate the number of goods
		Integer min = this.getMinGoods();
		if(min == null) min = 1;
		Integer max = this.getMaxGoods();
		if(max == null) max = ps.size();
		if(max < min) max = min;
		if(max > ps.size()) max = ps.size();

		//~: the sub-list of the prices
		int num = min + ctx.gen().nextInt(max - min + 1);
		ps = ps.subList(0, num);

		//~: the result prices
		List<GoodPrice> res = new ArrayList<>(ps.size());
		for(GoodPrice gp : ps)
		{
			GoodPrice x; res.add(x = new GoodPrice());
			x.setGoodUnit(gp.getGoodUnit());
			x.setPrice(EX.assertn(gp.getPrice()));
		}

		return res;
	}
}