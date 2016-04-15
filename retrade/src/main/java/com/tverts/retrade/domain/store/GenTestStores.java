package com.tverts.retrade.domain.store;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: actions */

import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.support.LU;


/**
 * Generates test {@link TradeStore}s.
 *
 * @author anton.baukin@gmail.com
 */
public class GenTestStores extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		//~: create the test trade store
		createTestTradeStores(ctx);

		//~: ensure the test trade store
		ensureTestTradeStores(ctx);
	}


	/* protected: test instances generation & verification */

	protected static final String   CODE_PREFIX   =
	  "СКЛ-";

	protected static final String[] CODE_SUFFICES = new String[]
	{
	  "ОСН", "А", "Б", "В"
	};


	protected void setTestTradeStores(GenCtx ctx, List<TradeStore> tss)
	{
		ctx.set(tss.toArray(new TradeStore[tss.size()]));
	}

	protected void createTestTradeStores(GenCtx ctx)
	{
		List<TradeStore> tss = bean(GetTradeStore.class).
		  getTradeStores(ctx.get(Domain.class).getPrimaryKey());

		//?: {test stores exist} do nothing
		if(!tss.isEmpty())
		{
			setTestTradeStores(ctx, tss);
			return;
		}

		tss = new ArrayList<TradeStore>(CODE_SUFFICES.length);

		//c: for all codes of test stores
		for(String cs : CODE_SUFFICES)
		{
			TradeStore ts = new TradeStore();
			setPrimaryKey(session(), ts, true);

			tss.add(ts);

			//~: domain & code
			ts.setDomain(ctx.get(Domain.class));
			ts.setCode(CODE_PREFIX + cs);

			//~: name
			ts.setName("Склад " + cs);

			//!: do save
			actionRun(ActTradeStore.SAVE, ts);

			//~: log success
			if(LU.isI(log(ctx)))
			{
				LU.I(log(ctx), logsig(),
				  " created test TradeStore with code [", ts.getCode(),
				  "] and pk [", ts.getPrimaryKey(), "]");
			}
		}

		setTestTradeStores(ctx, tss);
	}

	protected void ensureTestTradeStores(GenCtx ctx)
	{
		List<TradeStore> tss = bean(GetTradeStore.class).
		  getTradeStores(ctx.get(Domain.class).getPrimaryKey());

		//~: do ensure all the stores present
		for(TradeStore ts : tss)
			actionRun(ActTradeStore.ENSURE, ts);
	}
}