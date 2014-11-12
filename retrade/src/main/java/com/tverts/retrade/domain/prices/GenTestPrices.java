package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GenTestGoods;
import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Generates Price Lists based on the test
 * goods file 'GenTestGoods.xml'.
 *
 * If Price List already exists, nothing is done.
 * The initial prices generation is made via Price
 * Change Documents {@link RepriceDoc}.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class GenTestPrices extends GenesisHiberPartBase
{
	/* Genesis */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		Map<String, BigDecimal> costs = new HashMap<>(101);

		//~: read prices
		readTestPrices(ctx, costs);
		EX.assertx(!costs.isEmpty(), "Test goods has no costs!");

		//~: generate the lists
		genPriceLists(ctx, costs);
	}


	/* Generate Prices */

	@Param(required = true)
	public String getCodesAndNames()
	{
		return codesAndNames;
	}

	private String codesAndNames;

	public void setCodesAndNames(String cns)
	{
		this.codesAndNames = cns;
	}

	@Param
	public int getMinListGoods()
	{
		return minListGoods;
	}

	private int minListGoods = 20;

	public void setMinListGoods(int n)
	{
		EX.assertx((n > 0) & (n < 100));
		this.minListGoods = n;
	}

	@Param
	public int getMaxListGoods()
	{
		return maxListGoods;
	}

	private int maxListGoods = 50;

	public void setMaxListGoods(int n)
	{
		EX.assertx((n > 0) & (n < 100));
		this.maxListGoods = n;
	}

	@Param
	public int getCostsDelta()
	{
		return costsDelta;
	}

	private int costsDelta = 20;

	public void setCostsDelta(int n)
	{
		EX.assertx((n > 0) & (n < 100));
		this.costsDelta = n;
	}


	/* protected: generation */

	protected URL  getGoodsFile()
	{
		return EX.assertn(
		  GenTestGoods.class.getResource("GenTestGoods.xml"),
		  "No GenTestGoods.xml file found!"
		);
	}

	protected void readTestPrices(GenCtx ctx, Map<String, BigDecimal> costs)
	  throws GenesisError
	{
		try
		{
			//~: create the processor
			ReadTestGoods p = createProcessor(ctx);

			//~: do process
			p.process(getGoodsFile().toString());

			//~: add the resulting costs
			costs.putAll(p.getCosts());
		}
		catch(Throwable e)
		{
			e = EX.xrt(e);

			if(e instanceof GenesisError)
				throw (GenesisError)e;
			else
				throw new GenesisError(e, this, ctx);
		}
	}

	protected void genPriceLists(GenCtx ctx, Map<String, BigDecimal> costs)
	  throws GenesisError
	{
		//~: decode the names
		String[] pls = SU.s2a(getCodesAndNames());
		EX.assertx(pls.length   >= 2);
		EX.assertx(pls.length%2 == 0);

		//~: create main price list
		PriceListEntity pl = genPriceList(ctx, pls[0], pls[1], costs);
		ctx.set(pl); //<-- remember the main price list

		//~: all the lists
		Map<String, PriceListEntity> all =
		  new LinkedHashMap<>(pls.length / 2);
		all.put(pl.getCode(), pl); //<-- include the main list also

		//~: generate else lists
		for(int i = 2;(i < pls.length);i+=2)
		{
			//~: select random goods
			Map<String, BigDecimal> xcosts = new HashMap<>(costs);
			selectPriceListItems(ctx, xcosts);

			//~: generate the list
			pl = genPriceList(ctx, pls[i], pls[i + 1], xcosts);
			EX.assertx( all.put(pl.getCode(), pl) == null,
			  "Price List code [", pl.getCode(), "] is generated twice!");
		}

		//~: remember the lists
		ctx.set(PriceListEntity[].class,
		  all.values().toArray(new PriceListEntity[all.size()]));
	}

	protected PriceListEntity genPriceList
	  (GenCtx ctx, String code, String name, Map<String, BigDecimal> costs)
	{
		PriceListEntity pl = bean(GetGoods.class).getPriceList(
		  ctx.get(Domain.class).getPrimaryKey(), EX.asserts(code)
		);

		//?: {has this list} skip the generation
		if(pl != null) return pl;

		//~: create the new price list
		pl = new PriceListEntity();

		//=: domain
		pl.setDomain(ctx.get(Domain.class));

		//=: code
		pl.getOx().setCode(code);

		//=: name
		pl.getOx().setName(EX.asserts(name));

		//!: save the price list
		pl.updateOx();
		ActionsPoint.actionRun(ActionType.SAVE, pl);

		//~: create the price change document
		RepriceDoc rd = genRepriceDoc(ctx, pl, costs);

		LU.I(log(ctx), logsig(), " created test Price List [",
		  pl.getPrimaryKey(), "] with code [", pl.getCode(),
		  "] having [", costs.size(), "] items; and Price Change Document [",
		  rd.getPrimaryKey(), "] with code [", rd.getCode(), "]"
		);

		return pl;
	}

	@SuppressWarnings("unchecked")
	protected RepriceDoc genRepriceDoc
	  (GenCtx ctx, PriceListEntity pl, Map<String, BigDecimal> costs)
	{
		Map<String, GoodUnit> gm = (Map<String, GoodUnit>)
		  EX.assertn(ctx.get((Object) GoodUnit.class));

		//~: create the empty document first
		RepriceDoc rd = new RepriceDoc();
		initRepriceDoc(ctx, pl, rd);

		//!: save the document
		ActionsPoint.actionRun(ActionType.SAVE, rd,
		  ActionsPoint.UNITY_TYPE, Prices.TYPE_REPRICE_DOC
		);

		//~: create the items for all the goods
		rd.getChanges().clear();
		for(String gcode : costs.keySet())
		{
			GoodUnit gu = EX.assertn(gm.get(gcode),
			  "Good Unit with code [", gcode, "] is not found!"
			);

			//~: price change
			PriceChange pc = new PriceChange();
			rd.getChanges().add(pc);

			//~: good unit
			pc.setGoodUnit(gu);

			//~: cost
			BigDecimal c = EX.assertn(costs.get(gcode));
			if(c.scale() < 2) c = c.setScale(2);

			EX.assertx(CMP.grZero(c));
			EX.assertx(c.scale() == 2, "Cost of good [", gcode,
			  "] has more than two decimals after the point!");

			//=: price (cost)
			pc.setPriceNew(c);
		}

		//!: fix the document prices
		ActionsPoint.actionRun(Prices.ACT_FIX_PRICES, rd,
		  Prices.CHANGE_TIME, DU.merge(ctx.get(Date.class), new Date())
		);

		return rd;
	}

	protected void initRepriceDoc(GenCtx ctx, PriceListEntity pl, RepriceDoc rd)
	{
		//=: set test primary key
		setPrimaryKey(ctx.session(), rd, true);

		//=: domain
		rd.setDomain(ctx.get(Domain.class));

		//=: price list
		rd.setPriceList(pl);

		//=: generate document code
		rd.setCode(Prices.createRepriceDocCode(pl));

		//~: change reason
		rd.getOx().setRemarks(SU.cats(
		  "Первичная тестовая генерация цен для прайс-листа [",
		  pl.getCode(), "] от ", DU.date2str(ctx.get(Date.class))
		));
	}

	protected void selectPriceListItems(GenCtx ctx, Map<String, BigDecimal> costs)
	{
		//~: goods number
		int m = minListGoods;
		int M = maxListGoods;
		int s = costs.size();
		EX.assertx((m > 0) & (m <= M) & (m <= 100));
		s = (m + ctx.gen().nextInt(M - m + 1)) * s / 100;

		//~: the goods list
		List<String> goods = new ArrayList<>(costs.keySet());
		Collections.shuffle(goods, ctx.gen());
		goods = goods.subList(0, s);

		//!: retain the goods selected
		costs.keySet().retainAll(new HashSet<>(goods));

		//~: variate the costs values
		for(Map.Entry<String, BigDecimal> e : costs.entrySet())
		{
			//~: cost
			BigDecimal c = EX.assertn(e.getValue());
			EX.assertx(CMP.grZero(c));

			//~: variate the cost value
			int d = 100 - costsDelta + ctx.gen().nextInt(2*costsDelta + 1);
			c = c.multiply(new BigDecimal(d)).scaleByPowerOfTen(-2);

			//?: {scale the cost to .XY}
			if(c.scale() != 2)
				c = c.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			e.setValue(c);
		}
	}


	/* protected: XML Processor */

	protected ReadTestGoods createProcessor(GenCtx ctx)
	{
		return new ReadTestGoods(ctx);
	}

	protected static class GenState
	{
		public BigDecimal cost;
	}

	protected static class ReadTestGoods extends SaxProcessor<GenState>
	{
		public ReadTestGoods(GenCtx ctx)
		{
			this.ctx = ctx;
		}


		/* Processing Data */

		public Map<String, BigDecimal> getCosts()
		{
			return costs;
		}


		/* protected: SaxProcessor interface */

		protected void createState()
		{
			//?: <measure>
			if(istag(1, "good"))
				event().state(new GenState());
		}

		protected void open()
		{
			//?: <good>
			if(istag(1, "good"))
			{
				String cost = SU.s2s(event().attr("cost"));

				if(cost != null)
					event().state().cost =
					  new BigDecimal(cost).setScale(2);
			}
		}

		protected void close()
		{
			//?: <good> <code>
			if(istag(1, "good", "code"))
			{
				EX.assertn(state(1));

				//?: {has cost} add good
				if(state(1).cost != null)
					costs.put(EX.asserts(event().text()), state(1).cost);
			}
		}


		/* Genesis Context */

		protected final GenCtx            ctx;
		protected Map<String, BigDecimal> costs = new HashMap<>(101);
	}
}