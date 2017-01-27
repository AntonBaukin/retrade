package com.tverts.retrade.exec.api.goods;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: system transactions */

import com.tverts.retrade.domain.prices.GetPrices;
import com.tverts.system.tx.TxPoint;

/* com.tverts: actions */

import com.tverts.actions.ActionType;
import com.tverts.actions.ActionsPoint;

/* com.tverts: retrade domain (goods + prices) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.prices.PriceListEntity;

/* com.tverts: retrade api */

import com.tverts.api.retrade.prices.PriceItem;

/* com.tverts: execution (api) */

import com.tverts.api.core.Holder;
import com.tverts.exec.api.InsertEntityBase;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;


/**
 * Inserts or updates Good Price in a List.
 *
 * @author anton.baukin@gmail.com
 */
public class InsertPrice extends InsertEntityBase
{
	/* protected: InsertEntityBase interface */

	protected boolean isKnown(Holder holder)
	{
		return (holder.getEntity() instanceof PriceItem);
	}

	protected Long    insert(Object source)
	{
		PriceItem i = (PriceItem) source;

		//~: take the price list
		PriceListEntity l = EX.assertn( bean(GetPrices.class).getPriceList(
		  EX.assertn(i.getList(), "Price Item x-key [", i.getXkey(),
		    "] has no Price List primary key!")),

		  "Price Item x-key [", i.getXkey(), "] refers Price List p-key [",
		  i.getList(), "] not found!"
		);

		//~: check the price list domain
		checkDomain(l);

		//~: select price for the good given
		GoodPrice p;

		//~: good unit must be specified
		EX.assertn(i.getGood(), "Price Item x-key [", i.getXkey(),
		  "] has no Good Unit p-key specified!"
		);

		//~: price must be specified
		EX.assertx(
		  (i.getPrice() != null) && CMP.grZero(i.getPrice()),
		  "Price Item x-key [", i.getXkey(), "] has illegal price value!"
		);

		//?: {has good price key}
		if(i.getPkey() != null)
		{
			//~: select good price
			p = EX.assertn(bean(GetPrices.class).getGoodPrice(i.getPkey()),
			  "Good Price for Item with p-key [", i.getPkey(), "] not found!"
			);

			//~: check the good unit is the same
			EX.assertx(CMP.eq(p.getGoodUnit().getPrimaryKey(), i.getGood()),
			  "Price Item with p-key [", i.getPkey(),
			  "] has Good Unit changed from p-key [",
			  p.getGoodUnit().getPrimaryKey(),
			  "] to p-key [", i.getGood(), "]!"
			);
		}
		//~: select by the good
		else
			p = bean(GetPrices.class).getGoodPrice(l.getPrimaryKey(), i.getGood());

		//?: {good price not found} create it and save
		if(p == null)
		{
			p = new GoodPrice();

			//~: price list
			p.setPriceList(l);

			//~: good unit
			p.setGoodUnit(EX.assertn(bean(GetGoods.class).getGoodUnit(i.getGood()),
			  "Price Item with x-key [", i.getXkey(), "] has Good Unit p-key [",
			  i.getGood(), "] not found!"
			));

			//~: check good domain
			checkDomain(p.getGoodUnit());

			//~: price value
			p.setPrice(i.getPrice());

			//!: do save
			ActionsPoint.actionRun(ActionType.SAVE, p);
		}
		//~: just update the price
		else
		{
			//~: price value
			p.setPrice(i.getPrice());

			//~: update transaction number
			TxPoint.txn(tx(), p);
		}

		return p.getPrimaryKey();
	}
}