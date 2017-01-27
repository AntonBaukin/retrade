package com.tverts.retrade.domain.firm;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: objects */

import com.tverts.objects.Param;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.ActFirmPrices;
import com.tverts.retrade.domain.prices.PriceListEntity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;


/**
 * Relates each Contractor with the main
 * Price List and zero or more additional.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestFirmPrices extends GenesisHiberPartBase
{
	/* Genesis */

	@SuppressWarnings("unchecked")
	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: get the contractors
		Map<String, Contractor> cm = (Map<String, Contractor>)
		  ctx.get((Object) Contractor.class);
		EX.assertx(!cm.isEmpty(), "Domian has no test Contractors generated!");

		//~: just created contractors
		Set<Contractor> cx = (Set<Contractor>)
		  ctx.get(GenTestContractors.CREATED_CONTRACTORS);
		if(cx == null) cx = Collections.emptySet();

		//c: assign for each new contractor
		for(Contractor c : cm.values())
			if(cx.contains(c))
				assignPrices(ctx, c);
	}


	/* Associate Prices with Firms */

	@Param
	public int getMinFirmPrices()
	{
		return minFirmPrices;
	}

	private int minFirmPrices = 0;

	public void setMinFirmPrices(int n)
	{
		EX.assertx(n >= 0);
		this.minFirmPrices = n;
	}

	@Param
	public int getMaxFirmPrices()
	{
		return maxFirmPrices;
	}

	private int maxFirmPrices = 3;

	public void setMaxFirmPrices(int n)
	{
		EX.assertx(n >= 0);
		this.maxFirmPrices = n;
	}


	/* protected: generation */

	protected void assignPrices(GenCtx ctx, Contractor c)
	{
		//~: main price list
		PriceListEntity pl = EX.assertn(
		  ctx.get(PriceListEntity.class),
		  "No main Price List is generated!"
		);

		//~: all price lists
		PriceListEntity[] all = ctx.get(PriceListEntity[].class);
		EX.asserte(all, "No Price Lists were generated!");

		//?: {first is the main}
		EX.assertx(pl.equals(all[0]));

		//~: the limits
		int m = getMinFirmPrices();
		int M = getMaxFirmPrices();
		EX.assertx((m >= 0) & (m <= M));

		//HINT: all lists contains the main list also

		if(m > all.length - 1) m = all.length - 1;
		if(M > all.length - 1) M = all.length - 1;
		if(M < m) M = m;

		//~: random number of lists
		int n = m + ctx.gen().nextInt(M - m + 1);

		//~: the lists to assign
		List<PriceListEntity> pls = new ArrayList<PriceListEntity>(
		  Arrays.asList(all).subList(1, all.length));
		Collections.shuffle(pls, ctx.gen());
		pls = pls.subList(0, n);

		//~: main price list
		pls.add(0, pl); //<-- always the first, smallest priority

		//!: assign
		actionRun(ActFirmPrices.ASSIGN, c, ActFirmPrices.LISTS, pls);

		//~: log the lists
		List<String> codes = new ArrayList<String>(pls.size());
		for(PriceListEntity x : pls) codes.add(x.getCode());

		LU.I(log(ctx), logsig(), " Contractor [", c.getCode(),
		  "] has the following Price Lists assigned: [", SU.scats(", ", codes), "]"
		);
	}
}