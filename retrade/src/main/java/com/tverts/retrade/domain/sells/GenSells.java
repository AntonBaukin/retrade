package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;
import com.tverts.actions.ActionType;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: genesis */

import com.tverts.genesis.DaysGenDisp;
import com.tverts.genesis.DaysGenPart;
import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: retrade domain (goods + prices + stores) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Generates {@link SellsSession} instances with all
 * their {@link SellReceipt}s and {@link GoodSell}s.
 *
 * @author anton.baukin@gmail.com
 */
public class      GenSells
       extends    GenesisHiberPartBase
       implements DaysGenPart
{
	/* public: parameters interface */

	@Param
	public int getMinLengthMins()
	{
		return minLengthMins;
	}

	public void setMinLengthMins(int minLengthMins)
	{
		this.minLengthMins = minLengthMins;
	}

	@Param
	public int getMaxLengthMins()
	{
		return maxLengthMins;
	}

	public void setMaxLengthMins(int maxLengthMins)
	{
		this.maxLengthMins = maxLengthMins;
	}

	@Param
	public int getReceiptsMin()
	{
		return receiptsMin;
	}

	public void setReceiptsMin(int receiptsMin)
	{
		this.receiptsMin = receiptsMin;
	}

	@Param
	public int getReceiptsMax()
	{
		return receiptsMax;
	}

	public void setReceiptsMax(int receiptsMax)
	{
		this.receiptsMax = receiptsMax;
	}

	@Param
	public int getCashPayWeight()
	{
		return cashPayWeight;
	}

	public void setCashPayWeight(int cashPayWeight)
	{
		this.cashPayWeight = cashPayWeight;
	}

	@Param
	public int getBankPayWeight()
	{
		return bankPayWeight;
	}

	public void setBankPayWeight(int bankPayWeight)
	{
		this.bankPayWeight = bankPayWeight;
	}

	@Param
	public int getMixedPayPercent()
	{
		return mixedPayPercent;
	}

	public void setMixedPayPercent(int p)
	{
		EX.assertx((p >= 0) && (p <= 100));
		this.mixedPayPercent = p;
	}

	@Param
	public int getGoodsMin()
	{
		return goodsMin;
	}

	public void setGoodsMin(int goodsMin)
	{
		this.goodsMin = goodsMin;
	}

	@Param
	public int getGoodsMax()
	{
		return goodsMax;
	}

	public void setGoodsMax(int goodsMax)
	{
		this.goodsMax = goodsMax;
	}

	@Param
	public int getVolumeMin()
	{
		return volumeMin;
	}

	public void setVolumeMin(int volumeMin)
	{
		this.volumeMin = volumeMin;
	}

	@Param
	public int getVolumeMax()
	{
		return volumeMax;
	}

	public void setVolumeMax(int volumeMax)
	{
		this.volumeMax = volumeMax;
	}

	@Param
	public int getStoresMin()
	{
		return storesMin;
	}

	public void setStoresMin(int storesMin)
	{
		this.storesMin = storesMin;
	}

	@Param
	public int getStoresMax()
	{
		return storesMax;
	}

	public void setStoresMax(int storesMax)
	{
		this.storesMax = storesMax;
	}


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		SellsSession s = new SellsSession();

		//~: set test primary key
		setPrimaryKey(session(), s, true);

		//~: assign test domain
		s.setDomain(ctx.get(Domain.class));

		//~: session open timestamp
		s.setTime(ctx.get(DaysGenDisp.TIME, Date.class));

		//~: session close timestamp
		s.setCloseTime(genCloseTime(ctx, s));

		//~: sells desk
		s.setSellsDesk(selectSellsDesk(ctx));

		//~: payments desk
		s.setPayDesk(s.getSellsDesk().getPayDesk());

		//~: session code
		s.setCode(genSessionCode(ctx, s));

		//~: receipts generation
		genReceipts(ctx, s);

		//~: remarks
		s.setRemarks(genSessionRemarks(ctx, s));

		//~: save it
		save(ctx, s);
	}


	/* public: DaysGenPart interface */

	public boolean isDayClear(GenCtx ctx)
	{
		return super.isGenDispDayClear(ctx, Sells.typeSellsSession());
	}

	public void    markDayGenerated(GenCtx ctx)
	{
		super.markGenDispDay(ctx, Sells.typeSellsSession());
	}


	/* protected: session generation */

	protected void       save(GenCtx ctx, SellsSession s)
	{
		actionRun(ActionType.SAVE, s);
	}

	protected Date       genCloseTime(GenCtx ctx, SellsSession s)
	{
		EX.assertx((minLengthMins > 0) && (minLengthMins <= maxLengthMins));
		int  l = minLengthMins + ctx.gen().nextInt(maxLengthMins - minLengthMins + 1);
		long t = s.getTime().getTime() + l * 60 * 1000;

		return new Date(t);
	}

	protected SellsDesk  selectSellsDesk(GenCtx ctx)
	{
		//~: take the sells desk present
		SellsDesk[] desks = ctx.get(SellsDesk[].class);
		EX.asserte(desks, "No Sells Desks are found in this test Domain!");

		return EX.assertn(desks[ctx.gen().nextInt(desks.length)]);
	}

	protected String     genSessionCode(GenCtx ctx, SellsSession s)
	{
		return SU.cats(
		  s.getSellsDesk().getCode(), '-',
		  DU.date2strsys(s.getTime()),
		  '-', ctx.get(DaysGenDisp.DAYI)
		);
	}

	protected String     genSessionRemarks(GenCtx ctx, SellsSession s)
	{
		return SU.cats(
		  "Сгенерированная тестовая сессия продаж через терминал №",
		  s.getSellsDesk().getCode(), " от ", DU.datetime2str(s.getTime()),
		  ", содержащая ", s.getReceipts().size(), " чеков на общую сумму: ",
		  Sells.calcReceiptsIncome(s)
		);
	}


	/* protected: receipts generation */

	protected void       genReceipts(GenCtx ctx, SellsSession s)
	{
		//~: receipts number
		EX.assertx((receiptsMin > 0) && (receiptsMin < receiptsMax));

		int n = receiptsMin + ctx.gen().nextInt(receiptsMax - receiptsMin + 1);
		s.setReceipts(new ArrayList<SellReceipt>(n));

		//c: create the number of receipts
		for(int i = 0;(i < n);i++)
		{
			SellReceipt r = new SellReceipt();
			s.getReceipts().add(r);

			//~: primary key
			setPrimaryKey(session(), r, true);

			//~: session
			r.setSession(s);

			//~: transaction number
			TxPoint.txn(r);

			//~: receipt time
			r.setTime(genReceiptTime(ctx, s, i, n));

			//~: receipt code
			r.setCode(s.getCode() + '-' + (i + 1));

			//~: generate the goods
			genReceiptGoods(ctx, r);
			EX.asserte(r.getGoods());
			EX.assertn(r.getIncome());

			//~: calculate the payment
			genReceiptPayOps(ctx, r, i);
		}
	}

	protected Date       genReceiptTime(GenCtx ctx, SellsSession s, int i, int n)
	{
		long t0 = s.getTime().getTime();
		long t1 = s.getCloseTime().getTime();
		int  dt = (int)((t1 - t0)/(n * 1000)); //<-- in seconds

		long tx = t0 + 1000L*(dt*i + ctx.gen().nextInt(dt));
		return new Date(tx);
	}

	protected void       genReceiptPayOps(GenCtx ctx, SellReceipt r, int i)
	{
		SellPayOp op = new SellPayOp();

		//~: desk index code
		op.setDeskIndex(SU.lenum(4, (i + 1)));

		//~: separate between payment options
 		selectPayOps(ctx, r, op);

		//!: assign the operation
		r.payOp(op);
	}

	protected void       selectPayOps(GenCtx ctx, SellReceipt r, SellPayOp op)
	{
		BigDecimal cash = BigDecimal.ZERO;
		BigDecimal bank = BigDecimal.ZERO;

		//?: {mixed payment}
		if(ctx.gen().nextInt(100) < mixedPayPercent)
		{
			int x, s = cashPayWeight + bankPayWeight;

			for(GoodSell g : r.getGoods())
			{
				//~: select cash Vs bank
				x = ctx.gen().nextInt(s);

				//?: {pay via cash}
				if(x < cashPayWeight)
				{
					cash = cash.add(EX.assertn(g.getCost()));
					g.setPayFlag('C');
				}
				else
				{
					bank = bank.add(EX.assertn(g.getCost()));
					g.setPayFlag('B');
				}
			}
		}
		//~: select one of the ways
		else
		{
			int s = cashPayWeight + bankPayWeight;
			int x = ctx.gen().nextInt(s);

			//?: {pay via cash}
			if(x < cashPayWeight)
			{
				cash = r.getIncome();

				for(GoodSell g : r.getGoods())
					g.setPayFlag('C');
			}
			//~: pay via bank card
			else
			{
				bank = r.getIncome();

				for(GoodSell g : r.getGoods())
					g.setPayFlag('B');
			}
		}

		//~: assign them
		op.setPayCash(cash);
		op.setPayBank(bank);
	}

	protected void       genReceiptGoods(GenCtx ctx, SellReceipt r)
	{
		//~: good units number
		GoodUnit[] gunits = selectGoods(ctx);
		EX.asserte(gunits, "There are no Good Units with prices!");

		int        gcount = goodsMax - goodsMin + 1;

		EX.asserte(gunits);
		EX.assertx((goodsMin > 0) && (goodsMin <= goodsMax));

		if(gcount > gunits.length) gcount = gunits.length;
		gcount = goodsMin + ctx.gen().nextInt(gcount);
		if(gcount > gunits.length) gcount = gunits.length;

		//~: good units selection
		List<GoodUnit> gsel = new ArrayList<GoodUnit>(Arrays.asList(gunits));
		Collections.shuffle(gsel, ctx.gen());
		gsel = gsel.subList(0, gcount);

		//~: stores random selection
		List<TradeStore> stores = new ArrayList<TradeStore>(
		  Arrays.asList(ctx.get(TradeStore[].class)));
		EX.asserte(stores);
		Collections.shuffle(stores, ctx.gen());
		EX.assertx((storesMin > 0) && (storesMin <= storesMax));
		stores = stores.subList(0, storesMin +
		  ctx.gen().nextInt(storesMax - storesMin + 1));

		//~: total cost
		BigDecimal total = BigDecimal.ZERO;

		//c: create the goods of the number
		r.setGoods(new ArrayList<GoodSell>(gcount));
		for(int i = 0;(i < gcount);i++)
		{
			GoodSell gs = new GoodSell();

			//~: primary key
			setPrimaryKey(session(), gs, true);

			//~: receipt
			gs.setReceipt(r);
			r.getGoods().add(gs);

			//~: good unit
			gs.setGoodUnit(gsel.get(i));

			//~: next tore of the random selection
			gs.setStore(stores.get(i % stores.size()));

			//~: good volume
			gs.setVolume(genGoodSellVolume(ctx, gs));

			//~: set good cost (with the price by the price list)
			assignGoodSellCost(ctx, gs);

			//~: add to the receipt total
			total = total.add(gs.getCost());
		}

		//!: set receipt total income
		r.setIncome(total.setScale(5));
	}

	protected BigDecimal genGoodSellVolume(GenCtx ctx, GoodSell gs)
	{
		int v = volumeMin + ctx.gen().nextInt(volumeMax - volumeMin);

		if(gs.getGoodUnit().getMeasure().getOx().isFractional())
			return new BigDecimal("" + v + '.' + ctx.gen().nextInt(1000)).setScale(3);
		else
			return BigDecimal.valueOf(v);
	}

	protected GoodUnit[] selectGoods(GenCtx ctx)
	{
		//HINT: we do not cache the prices as they may change
		//  during the day-by-day generation!

		//~: take all the goods
		List<GoodUnit> goods = new ArrayList<GoodUnit>(
		  Arrays.asList(ctx.get(GoodUnit[].class))
		);

		Set<Long>      ids   = new HashSet<Long>(bean(GetPrices.class).
		   getGoodsWithPrices(ctx.get(Domain.class).getPrimaryKey()));

		for(Iterator<GoodUnit> i = goods.iterator();(i.hasNext());)
				if(!ids.contains(i.next().getPrimaryKey()))
					i.remove();

		return goods.toArray(new GoodUnit[goods.size()]);
	}

	protected void       assignGoodSellCost(GenCtx ctx, GoodSell gs)
	{
		GoodPrice gp;

		//?: {has no price list} assign it
		if(gs.getPriceList() == null)
		{
			//~: select random price list
			gp = selectGoodSellPrice(ctx, gs);

			//=: assign it
			if(gp != null)
				gs.setPriceList(gp.getPriceList());
			else
				throw EX.ass("Good Unit [", gs.getGoodUnit().getPrimaryKey(),
				  "] has no a Good Price to assign!");
		}
		//~: take the price from the selected list
		else
		{
			gp = bean(GetPrices.class).getGoodPrice(
			  gs.getPriceList().getPrimaryKey(),
			  gs.getGoodUnit().getPrimaryKey()
			);

			if(gp == null)
				throw EX.ass("Good Unit [", gs.getGoodUnit().getPrimaryKey(),
				  "] has no a Good Price in the selected List [",
				  gs.getPriceList().getPrimaryKey(), "]!");
		}

		//~: good cost (volume * price)
		gs.setCost(gs.getVolume().multiply(gp.getPrice()).setScale(5));
	}

	protected GoodPrice  selectGoodSellPrice(GenCtx ctx, GoodSell gs)
	{
		//HINT: we do not cache the prices as they may change
		//  during the day-by-day generation!

		List<GoodPrice> gps = bean(GetPrices.class).
		  getGoodPrices(gs.getGoodUnit().getPrimaryKey());
		return gps.isEmpty()?(null):(gps.get(ctx.gen().nextInt(gps.size())));
	}


	/* generator parameters */

	private int minLengthMins   = 30;
	private int maxLengthMins   = 120;
	private int receiptsMin     = 2;
	private int receiptsMax     = 10;
	private int cashPayWeight   = 80;
	private int bankPayWeight   = 20;
	private int mixedPayPercent = 25;
	private int goodsMin        = 1;
	private int goodsMax        = 10;
	private int volumeMin       = 1;
	private int volumeMax       = 5;
	private int storesMin       = 1;
	private int storesMax       = 3;
}