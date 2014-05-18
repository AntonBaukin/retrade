package com.tverts.retrade.exec.api.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system transactions */

import com.tverts.system.tx.TxPoint;

/* com.tverts: api execution */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: retrade domain (goods + prices + sells + stores) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.sells.GetSells;
import com.tverts.retrade.domain.sells.GoodSell;
import com.tverts.retrade.domain.sells.SellPayOp;
import com.tverts.retrade.domain.sells.SellsSession;
import com.tverts.retrade.domain.store.GetTradeStore;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Adds Sell Receipt to the Session.
 *
 * Note that the Session is not updated:
 * derived Sells Invoices stay the same.
 *
 * Synchronization program must issue
 * Sell Sessions updates further.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public class InsertSellReceipt extends InsertEntityBase
{
	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof com.tverts.api.retrade.sells.SellReceipt);
	}

	protected Long    insert(Object source)
	{
		com.tverts.api.retrade.sells.SellReceipt    s =
		  (com.tverts.api.retrade.sells.SellReceipt) source;

		com.tverts.retrade.domain.sells.SellReceipt d =
		  new com.tverts.retrade.domain.sells.SellReceipt();

		//~: must be no p-key
		EX.assertx(s.getPkey() == null);

		//~: load the session
		d.setSession(loadSession(s));
		d.getSession().getReceipts().add(d);

		//~: primary key
		HiberPoint.setPrimaryKey(tx(), d,
		  HiberPoint.isTestInstance(d.getSession()));

		//~: code
		d.setCode(s.getCode());

		//~: desk index
		SellPayOp payop = new SellPayOp();
		payop.setDeskIndex(s.getDeskIndex());

		//~: time
		d.setTime(s.getTime());

		//~: bank and cash incomes
		BigDecimal bank = BigDecimal.ZERO;
		BigDecimal cash = BigDecimal.ZERO;

		//~: create the goods of the receipt
		d.setGoods(new ArrayList<GoodSell>(s.getGoods().size()));
		for(com.tverts.api.retrade.goods.GoodSell sg : s.getGoods())
		{
			GoodSell dg = new GoodSell();

			//~: receipt <-> good
			d.getGoods().add(dg);
			dg.setReceipt(d);

			//~: primary key
			HiberPoint.setPrimaryKey(tx(), dg,
			  HiberPoint.isTestInstance(d.getSession()));

			//~: load good unit
			dg.setGoodUnit(loadGood(s, sg));

			//~: load trade store
			dg.setStore(loadStore(s, sg));

			//~: set the good price
			if(sg.getList() != null)
				dg.setGoodPrice(loadGoodPrice(s, sg));

			//~: volume
			EX.assertx(CMP.grZero(sg.getVolume()));
			dg.setVolume(sg.getVolume());

			//~: cost
			EX.assertx(CMP.greZero(sg.getCost()));
			dg.setCost(sg.getCost());
			
			//~: income
			if("C".equals(sg.getPayFlag()))
				cash = cash.add(sg.getCost());
			else if("B".equals(sg.getPayFlag()))
				bank = bank.add(sg.getCost());
			else throw EX.ass("Illegal pay-flag!");

			//~: pay flag
			dg.setPayFlag(sg.getPayFlag().charAt(0));
		}

		//~: set the income
		payop.setPayCash(cash);
		payop.setPayBank(bank);

		//~: assign pay op + income is set there
		d.payOp(payop);

		//~: transaction number
		TxPoint.txn(tx(), d);
		TxPoint.txn(tx(), d.getSession());

		//!: save the receipt
		session().save(d);

		return d.getPrimaryKey();
	}


	/* protected: insert support */

	protected SellsSession loadSession(com.tverts.api.retrade.sells.SellReceipt s)
	{
		EX.assertn(s.getSession(), "Sell Receipt x-key [", s.getXkey(),
		  "] has Sells Session p-key undefined!"
		);

		SellsSession r = EX.assertn(
		  bean(GetSells.class).getSession(s.getSession()),

		  "Sell Receipt  x-key [", s.getXkey(),
		  "] refers unknown Sells Session p-key [",
		  s.getSession(), "]!"
		);

		//sec: check the domain
		checkDomain(r);

		return r;
	}

	protected GoodUnit loadGood(
	    com.tverts.api.retrade.sells.SellReceipt s,
	    com.tverts.api.retrade.goods.GoodSell    sg
	  )
	{
		EX.assertn( sg.getGood(),
		  "Sell Receipt x-key [", s.getXkey(),
		  "] has Good x-key [", sg.getXGood(),
		  "] with undefined p-key!"
		);

		GoodUnit gu = EX.assertn(
		  bean(GetGoods.class).getGoodUnit(sg.getGood()),

		  "Sell Receipt x-key [", s.getXkey(),
		  "] refers unknown Good x-key [", sg.getXGood(),
		  "], p-key [", sg.getGood(), "]!"
		);

		//sec: check the domain
		checkDomain(gu);

		return gu;
	}

	protected TradeStore loadStore(
	    com.tverts.api.retrade.sells.SellReceipt s,
	    com.tverts.api.retrade.goods.GoodSell    sg
	  )
	{
		EX.assertn( sg.getStore(),
		  "Sell Receipt x-key [", s.getXkey(),
		  "] good x-key [", sg.getXGood(),
		  "] has Store x-key [", sg.getXStore(),
		  "] with undefined p-key!"
		);

		TradeStore ts = EX.assertn(
		  bean(GetTradeStore.class).getTradeStore(sg.getStore()),

		  "Sell Receipt x-key [", s.getXkey(),
		  "] good x-key [", sg.getXGood(),
		  "] refers unknown Store x-key [", sg.getXStore(),
		  "], p-key [", sg.getStore(), "]!"
		);

		//sec: check the domain
		checkDomain(ts);

		return ts;
	}

	protected GoodPrice loadGoodPrice(
	    com.tverts.api.retrade.sells.SellReceipt s,
	    com.tverts.api.retrade.goods.GoodSell    sg
	  )
	{
		GoodPrice gp = EX.assertn(
		  bean(GetGoods.class).getGoodPrice(sg.getList(), sg.getGood()),

		  "Sell Receipt x-key [", s.getXkey(),
		  "] good x-key [", sg.getXGood(),
		  "] refers unknown Price List x-key [", sg.getXList(),
		  "] and p-key [", sg.getList(),
		  "] or this Price List has no position for the Good!"
		);

		//sec: check the domain
		checkDomain(gp.getPriceList());

		return gp;
	}
}