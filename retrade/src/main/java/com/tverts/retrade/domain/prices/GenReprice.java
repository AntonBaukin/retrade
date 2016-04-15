package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.Goods;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.DU;
import com.tverts.support.LU;


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

	@Param(descr = "The minimum number of the goods of the price list to change")
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

	@Param(descr = "The maximum number of the goods of the price list to change")
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

	@Param(descr = "Integer of the good price delta minimum percent " +
	  "(may be negative, not zero)")
	public int getCostsDeltaMin()
	{
		return costsDeltaMin;
	}

	private int costsDeltaMin = -5;

	public void setCostsDeltaMin(int n)
	{
		EX.assertx(n != 0);
		this.costsDeltaMin = n;
	}

	@Param(descr = "Integer of the good price delta maximum percent " +
	  "(must be positive, not zero)")
	public int getCostsDeltaMax()
	{
		return costsDeltaMax;
	}

	private int costsDeltaMax = 25;

	public void setCostsDeltaMax(int n)
	{
		EX.assertx(n > 0);
		this.costsDeltaMax = n;
	}

	@Param(descr = "Integer [0; 100] of the persent of the goods not in " +
	  "the main price list to just update, not delete or add")
	public int getUpdatePercent()
	{
		return updatePercent;
	}

	private int updatePercent = 80;

	public void setUpdatePercent(int n)
	{
		EX.assertx((n >= 0) & (n <= 100));
		this.updatePercent = n;
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
		GetGoods get = bean(GetGoods.class);

		  //~: select the changes
		  List < GoodPrice > changes = genSelectGoods(ctx, rd);
		EX.asserte(changes);
		EX.assertx(rd.getChanges().isEmpty());

		//~: target price list and the main
		PriceListEntity pl = EX.assertn(rd.getPriceList());
		PriceListEntity mn = EX.assertn(ctx.get(PriceListEntity.class));

		LU.I(log(ctx), "Price Change Document [", rd.getCode(),
		  "] for Price List [", pl.getCode(), "] contains the following [",
		  changes.size(), "] changes:"
		);

		//c: for each change
		for(GoodPrice gp : changes)
		{
			//~: price change
			PriceChange pc = new PriceChange();
			rd.getChanges().add(pc);

			//=: price change document
			pc.setRepriceDoc(rd);

			//=: good unit
			pc.setGoodUnit(gp.getGoodUnit());

			//?: {the good is removed}
			if(gp.getPrice() == null)
			{
				LU.I(log(ctx), "--> removed good [", gp.getGoodUnit().getCode(), "]");
				continue;
			}

			BigDecimal c; //<-- good new cost

			//?: {good has group}
			String g = get.getAttrString(pc.getGoodUnit(), Goods.AT_GROUP);
			if(g != null)
			{
				if(rd.getOx().getGroupChanges() == null)
					rd.getOx().setGroupChanges(new HashMap<>(11));

				BigDecimal p; if((p = rd.getOx().getGroupChanges().get(g)) == null)
				{
					p = genChangePercent(ctx);
					rd.getOx().getGroupChanges().put(g, p);
					LU.I(log(ctx), "--> good group [", g, "] = ", p, "%");
				}

				c = new BigDecimal(100).add(p);
			}
			//~: variate directly
			else
				c = new BigDecimal(100).add(genChangePercent(ctx));

			//~: multiply the final cost
			c = gp.getPrice().multiply(c).scaleByPowerOfTen(-2);

			//?: {scale the cost to .XY}
			if(c.scale() != 2)
				c = c.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			//?: {good is added (from the main list)}
			if(!CMP.eq(pl, mn) && CMP.eq(pl, gp.getPriceList()))
				LU.I(log(ctx), "--> added good [",
				  gp.getGoodUnit().getCode(), "], price: ", c);
				//~: the price was changed
			else
				LU.I(log(ctx), "--> re-price good [",
				  gp.getGoodUnit().getCode(), "] from: [",
				  gp.getPrice(), "] to: [", c, "]");

			//=: the new price
			pc.setPriceNew(c);
		}
	}

	protected List<GoodPrice> genSelectGoods(GenCtx ctx, RepriceDoc rd)
	{
		//HINT: main price list always contains the prices of all the goods
		//  to sell, and no good position may be removed or added there!

		//~: the target price list
		PriceListEntity pl = EX.assertn(rd.getPriceList());

		//~: the main price list
		PriceListEntity mn = EX.assertn(
		  ctx.get(PriceListEntity.class),
		  "No Main Price List is found (generated)!"
		);

		//~: all the goods from the main price list
		List<GoodPrice> ps = bean(GetPrices.class).getPriceListPrices(mn);
		EX.asserte(ps, "Main Price List has no prices generated!");

		//~: shuffle them
		Collections.shuffle(ps, ctx.gen());

		//~: the number of goods to process
		int num;
		{
			Integer min = this.getMinGoods();
			if(min == null) min = 1;
			Integer max = this.getMaxGoods();
			if(max == null) max = ps.size();
			if(max < min) max = min;
			if(max > ps.size()) max = ps.size();

			num = min + ctx.gen().nextInt(max - min + 1);
		}

		//?: {this is the main price list} variate the prices
		if(mn.equals(pl))
			//~: just a sub-list of the prices
			ps = ps.subList(0, num);
		//~: update some of the existing prices, add or remove
		else
		{
			//~: the resulting list
			List<GoodPrice> xr = new ArrayList<>(num);

			//~: map all the goods with the prices
			HashMap<Long, GoodPrice> pm = new HashMap<>(ps.size());
			for(GoodPrice p : ps)
				pm.put(p.getGoodUnit().getPrimaryKey(), p);

			//~: the current prices of the list
			List<GoodPrice> cr = bean(GetPrices.class).getPriceListPrices(pl);
			Collections.shuffle(cr, ctx.gen()); //<-- shuffle them

			//~: exclude them from add candidates
			for(GoodPrice p : cr)
				pm.remove(p.getGoodUnit().getPrimaryKey());

			//~: first, take the goods to just update
			int up = updatePercent * Math.min(cr.size(), num)  / 100;
			if(up != 0) xr.addAll(cr.subList(0, up));

			//~: then, process add-remove (what is possible)
			for(int i = xr.size();(i < num);i++)
			{
				//?: {add is possible}
				boolean ap = !pm.isEmpty();

				//?: {remove is possible}
				boolean rp = (up < cr.size());

				//?: {both are not possible}
				EX.assertx(ap | rp, "Both add and remove operatins are not possible ",
				  "for Price List [", pl.getPrimaryKey(), "] with code [", pl.getCode(), "]!");

				//?: {to add (false) or to remove (true)}
				boolean ar = (ap & rp)?(ctx.gen().nextBoolean()):(!ap);

				//?: {remove}
				if(ar)
				{
					//~: take the item existing in the price list
					GoodPrice p = cr.get(up++);
					GoodPrice x = new GoodPrice();

					x.setGoodUnit(p.getGoodUnit());
					x.setPriceList(p.getPriceList());
					x.setPrice(null); //<-- mark to remove it

					xr.add(x);
				}
				else
				{
					//~: take the item not existing in the list
					GoodPrice p = pm.values().iterator().next();
					pm.remove(p.getGoodUnit().getPrimaryKey());

					xr.add(p);
				}
			}

			//~: the resulting list
			ps = xr;
		}

		//~: the result prices
		List<GoodPrice> res = new ArrayList<>(ps.size());
		for(GoodPrice gp : ps)
		{
			GoodPrice x; res.add(x = new GoodPrice());
			x.setGoodUnit(gp.getGoodUnit());
			x.setPriceList(gp.getPriceList());
			x.setPrice(gp.getPrice()); //<-- price is null when removed
		}

		return res;
	}

	protected BigDecimal      genChangePercent(GenCtx ctx)
	{
		EX.assertx(costsDeltaMin < costsDeltaMax);

		int d = 0; while(d == 0)
			d = costsDeltaMin + ctx.gen().nextInt(costsDeltaMax - costsDeltaMin + 1);

		return new BigDecimal(d);
	}
}