package com.tverts.retrade.exec.api.sells;

/* com.tverts: hibery */

import com.tverts.hibery.qb.QueryBuilder;

/* com.tverts: retrade domain (sells) */

import com.tverts.retrade.domain.sells.GoodSell;

/* com.tverts: execution (api) */

import com.tverts.api.core.DumpEntities;
import com.tverts.exec.api.EntitiesDumperBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Dumps Sell Receipts in the same named API objects.
 *
 * @author anton.baukin@gmail.com.
 */
public class DumpSellReceipts extends EntitiesDumperBase
{
	protected Object createApiEntity(Object src)
	{
		com.tverts.retrade.domain.sells.SellReceipt s =
		  (com.tverts.retrade.domain.sells.SellReceipt)src;

		com.tverts.api.retrade.sells.SellReceipt    d =
		  new com.tverts.api.retrade.sells.SellReceipt();

		//~: primary key
		d.setPkey(s.getPrimaryKey());

		//~: tx-number
		d.setTx(s.getTxn());

		//~: sells session
		d.setSession(s.getSession().getPrimaryKey());

		//~: code
		d.setCode(s.getCode());

		//~: desk index
		d.setDeskIndex(s.payOp().getDeskIndex());

		//~: time
		d.setTime(s.getTime());

		//~: income
		d.setIncome(s.getIncome());

		//!: sells receipts are always fixed
		d.setFixed(true);

		//~: create the goods
		for(GoodSell g : s.getGoods())
		{
			com.tverts.api.retrade.goods.GoodSell x =
			  new com.tverts.api.retrade.goods.GoodSell();
			d.getGoods().add(x);

			//~: good
			x.setGood(g.getGoodUnit().getPrimaryKey());

			//~: price list
			if(g.getPriceList() != null)
				x.setList(g.getPriceList().getPrimaryKey());

			//~: trade store
			x.setStore(g.getStore().getPrimaryKey());

			//~: volume
			x.setVolume(g.getVolume());

			//~: volume cost
			x.setCost(g.getCost());

			//~: payment flag
			x.setPayFlag("" + g.getPayFlag());
		}

		return d;
	}

	protected Class  getUnityClass()
	{
		return com.tverts.retrade.domain.sells.SellReceipt.class;
	}

	protected Class  getEntityClass()
	{
		return com.tverts.api.retrade.sells.SellReceipt.class;
	}

	public void      setUnityType(String unityType)
	{
		throw EX.unop();
	}

	protected void   restrictDumpDomain(QueryBuilder qb, DumpEntities de)
	{
		//~: go through the session domain
		qb.getClauseWhere().addPart(
		  "e.session.domain = :domain"
		).
		  param("domain", tx().getDomain());
	}
}