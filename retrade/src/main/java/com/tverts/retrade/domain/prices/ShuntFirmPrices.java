package com.tverts.retrade.domain.prices;

/* Java */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: self shunts */

import com.tverts.shunts.SelfShuntDescr;
import com.tverts.shunts.SelfShuntGroups;
import com.tverts.shunts.SelfShuntMethod;
import com.tverts.shunts.SelfShuntUnit;
import com.tverts.shunts.ShuntPlain;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;
import com.tverts.retrade.domain.firm.GetContractor;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Checks that all Contractors have Price Lists
 * associated via {@link FirmPrices}, and the
 * {@link PriceCross}es are properly linked.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@SelfShuntUnit
@SelfShuntGroups({"retrade:goods", "retrade:goods:prices", "retrade:goods:prices:firms"})
@SelfShuntDescr("Checks all the Contractors have proper prices of the Goods.")
public class ShuntFirmPrices extends ShuntPlain
{
	/* Shunt Methods */

	@SelfShuntMethod(order = 0, critical = true)
	public void testFirmsHasPrices()
	{
		//~: select the contractors
		contractors = bean(GetContractor.class).
		  selectFirmContractors(domain().getPrimaryKey());

		EX.asserte(contractors, "Domain has no test Contractors (Firms) generated!");
		c2fps = new HashMap<Contractor, List<FirmPrices>>(contractors.size());

		//c: for each contractor
		for(Contractor c : contractors)
		{
			List<FirmPrices> fps = bean(GetPrices.class).
			  loadPrices(c.getPrimaryKey());

			EX.asserte(fps, "Contractor [", c,
			  "] has no Price Lists associated via Firm Prices!");

			for(int i = 0;(i < fps.size());i++)
				EX.assertx(i == fps.get(i).getPriority());

			c2fps.put(c, fps);
		}
	}

	protected List<Contractor> contractors;
	protected Map<Contractor, List<FirmPrices>> c2fps;

	@SelfShuntMethod(order = 1, critical = true)
	@SuppressWarnings("unchecked")
	public void testFirmsPrices()
	{
		EX.assertx(!c2fps.isEmpty());

		//~: select all the price lists
		priceLists = bean(GetPrices.class).getPriceLists(
		  domain().getPrimaryKey());
		EX.asserte(priceLists, "Domain has no test Price Lists generated!");

		//~: select the good prices for each list
		pl2g2p = new HashMap<Long, Map<Long, Long>>(priceLists.size());
		for(PriceListEntity pl : priceLists)
		{
			//~: select the prices
			Map<Long, Long> g2p = new HashMap<Long, Long>(101);
			bean(GetPrices.class).getPriceListPrices(pl.getPrimaryKey(), g2p);

			EX.assertx(!g2p.isEmpty(), "Price List [",
			  pl.getPrimaryKey(), "] has no good prices!");

			pl2g2p.put(pl.getPrimaryKey(), g2p);
		}

		//c: check associations for each contractor
		for(Contractor c : contractors)
		{
			List<FirmPrices> fps = EX.assertn(c2fps.get(c));
			Map<Long, Long>  x2y = new HashMap<Long, Long>(101);

			//~: collect manually the proper prices
			for(FirmPrices fp : fps)
			{
				Map<Long, Long> g2p = EX.assertn(pl2g2p.get(
				  fp.getPriceList().getPrimaryKey()));

				//~: overwrite the prices
				x2y.putAll(g2p);
			}

			//~: collect the existing prices
			List<Object[]> cps = (List<Object[]>) bean(GetPrices.class).
			  selectCurrentPrices(c.getPrimaryKey());

			//~: check one-by-one
			for(Object[] cp : cps)
			{
				Long g = (Long) cp[0]; //<-- good unit
				Long p = (Long) cp[1]; //<-- good price

				Long x = EX.assertn(x2y.get(g),
				  "Good Unit [", g, "] has no price for Contractor [",
				  c.getPrimaryKey(), "]!"
				);

				//?: {it item the same}
				if(!CMP.eq(p, x))
					throw EX.ass( "Good Unit [", g, "] has wrong price for Contractor [",
					  c.getPrimaryKey(), "]: is [", x, "], needed [", p, "]!");

				//~: remove the good
				x2y.remove(g);
			}

			//?: {not all the goods do have prices}
			EX.assertx(x2y.isEmpty(), "Contractor [", c.getPrimaryKey(),
			  "]: has the following goods without the prices: [",
			  SU.scats(", ", x2y.keySet()), "]!"
			);
		}
	}

	protected List<PriceListEntity> priceLists;

	//maps: price list -> (good -> price)
	protected Map<Long, Map<Long, Long>> pl2g2p;
}