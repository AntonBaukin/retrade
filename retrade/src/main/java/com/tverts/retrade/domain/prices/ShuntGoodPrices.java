package com.tverts.retrade.domain.prices;

/* Java */

import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: self shunts */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.DU;
import com.tverts.support.EX;


/**
 * Checks various issues relating the Goods
 * and the prices in the Price Lists.
 *
 * @author anton.baukin@gmail.com.
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:goods", "retrade:goods:prices"})
@SelfShuntDescr("Checks all the Goods have proper prices.")
public class ShuntGoodPrices extends ShuntPlain
{
	/* Shunt Methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testPricesHistory()
	{
		//~: select all the test goods
		this.get   = bean(GetPrices.class);
		this.goods = get.getGoodUnits(domain());
		EX.asserte(goods, "No test Good Units were generated!");

		//~: select all the price lists
		this.lists = get.getPriceLists(domain().getPrimaryKey());
		EX.asserte(lists, "No test Price Lists were generated!");

		//c: for each good check it's history in all the lists
		for(GoodUnit g : goods) for(PriceListEntity pl : lists)
		{
			//~: select all the changes
			List<PriceChange> cs = get.selectPriceHistory(
			  pl.getPrimaryKey(), g.getPrimaryKey());

			//~: select current price
			GoodPrice gp = get.getGoodPrice(
			  pl.getPrimaryKey(), g.getPrimaryKey());

			//?: {has no changes}
			if(cs.isEmpty())
			{
				//?: {has no price}
				EX.assertx(gp == null, "Good [", g.getPrimaryKey(),
				  "] with code [", g.getCode(), "] has price in the List [",
				  pl.getPrimaryKey(), "] with code [", pl.getCode(),
				  "] without any Price Change Document to exist!"
				);

				continue;
			}

			PriceChange x = cs.get(0);
			PriceChange y = cs.get(cs.size() - 1);

			//?: {first item must have no old price}
			EX.assertx(x.getPriceOld() == null);

			//?: {the last item has no current price}
			if(gp == null) EX.assertx((y.getPriceNew() == null),
			  "Good [", g.getPrimaryKey(), "] with code [", g.getCode(),
			  "] has price history in the List [", pl.getPrimaryKey(),
			  "] with code [", pl.getCode(), "] at time [",
			  DU.datetime2str(y.getChangeTime()), "] new value [", x.getPriceNew(),
			  "], but there is no price in the List!"
			);

			//?: {the last item is removed}
			if(y.getPriceNew() == null) EX.assertx((gp == null),
			  "Good [", g.getPrimaryKey(), "] with code [", g.getCode(),
			  "] has final price history in the List [", pl.getPrimaryKey(),
			  "] with code [", pl.getCode(), "] at time [",
			  DU.datetime2str(y.getChangeTime()),
			  "] removed, but the Good Price item [",
			  (gp == null)?(null):(gp.getPrimaryKey()),
			  "] is still in the List having price [",
			  (gp == null)?(null):(gp.getPrice()), "]!"
			);

			//?: {the last item has same new price}
			if(gp != null) EX.assertx(CMP.eq(gp.getPrice(), y.getPriceNew()),
			  "Good [", g.getPrimaryKey(), "] with code [", g.getCode(),
			  "] has price history in the List [", pl.getPrimaryKey(),
			  "] with code [", pl.getCode(), "] at time [",
			  DU.datetime2str(y.getChangeTime()), "] new value [", x.getPriceNew(),
			  "], but current price in the List is: [", gp.getPrice(), "]!"
			);

			//c: scan the changes
			for(int i = 1;(i < cs.size());i++)
			{
				x = cs.get(i-1); y = cs.get(i);

				EX.assertx(CMP.eq(x.getGoodUnit(),  y.getGoodUnit()));
				EX.assertx(CMP.eq(x.getPriceList(), y.getPriceList()));

				//?: {previous price equals to the new one}
				EX.assertx(CMP.eq(x.getPriceNew(), y.getPriceOld()),

				  "Good [", g.getPrimaryKey(), "] with code [", g.getCode(),
				  "] has wrong price history in the List [", pl.getPrimaryKey(),
				  "] with code [", pl.getCode(), "] at time [",
				  DU.datetime2str(x.getChangeTime()), "] old value [", x.getPriceNew(),
				  "] Vs at time [", DU.datetime2str(y.getChangeTime()), "] new value [",
				  y.getPriceOld(), "]!"
				);
			}
		}
	}

	protected GetPrices              get;
	protected List<GoodUnit>         goods;
	protected List<PriceListEntity>  lists;
}