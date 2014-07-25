package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;


/* com.tverts: actions */

import com.tverts.actions.ActionType;
import static com.tverts.actions.ActionsPoint.actionRun;

/* com.tverts: genesis */

import com.tverts.genesis.GenCtx;
import com.tverts.genesis.GenesisError;
import com.tverts.genesis.GenesisHiberPartBase;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;
import com.tverts.api.retrade.goods.CalcItem;
import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.Measure;

/* com.tverts: endure (core) */

import com.tverts.endure.core.Domain;

/* com.tverts: support */

import com.tverts.retrade.domain.goods.GenTestGoods;
import com.tverts.support.EX;
import com.tverts.support.LU;
import com.tverts.support.SU;
import com.tverts.support.xml.SaxProcessor;


/**
 * Generates Price Lists based on the test
 * goods file 'GenTestGoods.xml'.
 *
 * @author anton.baukin@gmail.com.
 */
public class GenTestPrices extends GenesisHiberPartBase
{
	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		Map<String, BigDecimal> costs = new HashMap<String, BigDecimal>(101);

		//~: read prices
		readTestPrices(ctx, costs);
		EX.assertx(!costs.isEmpty(), "Test goods has no costs!");


	}


	/* protected: generation */

	protected URL  getDataFile()
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
			p.process(getDataFile().toString());

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


	/* protected: XML Processor */

	protected ReadTestGoods createProcessor(GenCtx ctx)
	{
		return new ReadTestGoods(ctx);
	}

	protected static class GenState
	{
		public BigDecimal              cost;
	}

	protected class ReadTestGoods extends SaxProcessor<GenState>
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
					event().state().cost = new BigDecimal(cost).setScale(2);
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


		/* genesis context */

		private GenCtx                  ctx;
		private Map<String, BigDecimal> costs =
		  new HashMap<String, BigDecimal>(101);
	}
}