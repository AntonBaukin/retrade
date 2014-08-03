package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
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

/* com.tverts: retrade api */

import com.tverts.api.retrade.prices.PriceChanges;

/* com.tverts: retrade domain (goods + invoices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.gen.GenInvoiceBase;

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
		RepriceDoc   rd = new RepriceDoc();
		PriceChanges pc = rd.getOx();

		//=: set test primary key
		setPrimaryKey(session(), rd, true);

		//=: domain
		rd.setDomain(ctx.get(Domain.class));

		//=: generate document code
		rd.setCode(genRepriceDocCode(ctx));

		//~: select random price list
		PriceListEntity[] pls = ctx.get(PriceListEntity[].class);
		rd.setPriceList(pls[ctx.gen().nextInt(pls.length)]);

		//~: change reason
		pc.setRemarks(String.format(
		  "Тестовая генерация, день %s.",
		  DU.date2str(ctx.get(DaysGenDisp.DAY, Date.class))
		));

		//~: generate price changes
		genPriceChanges(ctx, rd);

		//~: save the document
		rd.updateOx();
		save(ctx, rd);

		//!: fix the prices now
		fixPrices(ctx, rd);
	}


	/* public: bean interface */

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


	/* public: DaysGenPart interface */

	public boolean    isDayClear(GenCtx ctx)
	{
		return super.isGenDispDayClear(ctx,
		  UnityTypes.unityType(RepriceDoc.class, Prices.TYPE_REPRICE_DOC)
		);
	}

	public void       markDayGenerated(GenCtx ctx)
	{
		super.markGenDispDay(ctx,
		  UnityTypes.unityType(RepriceDoc.class, Prices.TYPE_REPRICE_DOC)
		);
	}


	/* protected: price changes generation */

	protected String  genRepriceDocCode(GenCtx ctx)
	{
		return Prices.createRepriceDocCode(
		  ctx.get(DaysGenDisp.DAY, Date.class),
		  ctx.get(DaysGenDisp.DAYI, Integer.class)
		);
	}

	protected void    save(GenCtx ctx, RepriceDoc rd)
	{
		actionRun(ActionType.SAVE, rd);
	}

	protected void    fixPrices(GenCtx ctx, RepriceDoc rd)
	{
		actionRun(Prices.ACT_FIX_PRICES, rd,
		  Prices.CHANGE_TIME, ctx.get(DaysGenDisp.TIME, Date.class)
		);
	}

	protected void    genPriceChanges(GenCtx ctx, RepriceDoc rd)
	{
		GetGoods get = bean(GetGoods.class);
		int      ind = 0;

		for(GoodUnit good : genSelectGoods(ctx))
		{
			GoodPrice gp = get.getGoodPrice(rd.getPriceList(), good);

			//?: {good has no price in this price list} skip it
			if(gp == null) continue;

			PriceChange pc = new PriceChange();

			//~: set test primary key
			setPrimaryKey(session(), pc, true);

			//~: price change document
			pc.setRepriceDoc(rd);
			rd.getChanges().add(pc);

			//~: price list good
			pc.setGoodUnit(good);

			//~: index in the document
			pc.setDocIndex(ind++);

			//~: delta price in -5+10%
			BigDecimal d = BigDecimal.ONE.add(BigDecimal.
			  valueOf(-5 + ctx.gen().nextInt(16)).movePointLeft(2));

			pc.setPriceNew(gp.getPrice().multiply(d).
			  setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}
	}

	protected List<GoodUnit> genSelectGoods(GenCtx ctx)
	{
		//~: take all the goods
		List<GoodUnit> goods = new ArrayList<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class)));

		//~: remove the goods without prices
		GenInvoiceBase.retainGoodsWithPrices(ctx, goods);
		EX.asserte(goods);

		//~: shuffle them
		Collections.shuffle(goods, ctx.gen());

		//~: generate the number of goods
		Integer min = this.getMinGoods();
		if(min == null) min = 1;
		Integer max = this.getMaxGoods();
		if(max == null) max = goods.size();
		if(max < min) max = min;
		if(max > goods.size()) max = goods.size();

		int     num = min + ctx.gen().nextInt(max - min + 1);
		return goods.subList(0, num);
	}
}