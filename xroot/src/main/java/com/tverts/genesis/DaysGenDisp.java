package com.tverts.genesis;

/* Java */

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/* com.tverts: (spring + tx) */

import static com.tverts.spring.SpringPoint.bean;
import com.tverts.system.tx.TxBean;

/* com.tverts: objects */

import com.tverts.objects.ObjectParam;
import com.tverts.objects.ObjectParams;
import com.tverts.objects.Param;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.EX;
import com.tverts.support.LU;


/**
 * This generator dispatches creation of entities
 * day-by-day for the period defined. Starting from
 * the most old day it invokes all the generators
 * registered in the random order with the weights.
 *
 * Each sub-generator registered has the integer weight
 * assigned. The number of objects created in a day is
 * selected from random range. For each object the
 * generated is chosen by it's wight.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DaysGenDisp extends GenesisPartBase
{
	/* constants */

	/**
	 * Name of the parameter of {@link GenCtx}
	 * that defines the day of the generation.
	 */
	public static final String DAY  =
	  DaysGenDisp.class.getName() + ": day";

	/**
	 * Name of the parameter of {@link GenCtx}
	 * that defines the time of the object
	 * in the day based on it's in-day index.
	 *
	 * This index counter is shared across all
	 * the genesis entries, unlike the typed one
	 * stored by {@link #DAYI}.
	 */
	public static final String TIME =
	  DaysGenDisp.class.getName() + ": time";

	/**
	 * Name of the parameter of {@link GenCtx}
	 * that stores the index of the object
	 * within the day. Note that each genesis
	 * referred has distinct index counter.
	 */
	public static final String DAYI =
	  DaysGenDisp.class.getName() + ": day index";


	/* public: Genesis interface */

	public void generate(GenCtx ctx)
	  throws GenesisError
	{
		if((getEntries() == null) || (getEntries().length == 0))
			return;

		//~: the start day (inclusive)
		Date curDay  = findFirstGenDate(ctx);
		if(curDay == null) return;

		//~: the final day (inclusive)
		Date endDay  = findLastGenDate(ctx);
		if(endDay == null) return;

		//c: generate till the last day
		while(!curDay.after(endDay))
		{
			//~: set the day parameter of the context
			ctx.set(DAY, curDay);

			genObjectsTxDisp(ctx, curDay);
			curDay = DU.addDaysClean(curDay, +1);

			ctx.set(DAY, null);
		}

		//?: {has test} run it
		if(getTest() != null)
			getTest().testGenesis(ctx);
	}

	public void parameters(List<ObjectParam> params)
	{
		super.parameters(params);

		//~: collect the entries parameters
		for(Entry e : getEntries())
		{
			//~: add entry parameters
			ObjectParam[] eps = ObjectParams.find(e);
			StringBuilder sb  = new StringBuilder(32);
			for(ObjectParam ep : eps)
			{
				sb.delete(0, sb.length());

				//~: dispatcher (as a genesis) name
				sb.append(getGenesisParamPrefix(this));
				sb.append(" : [Entry] ");

				//~: prefix the name
				sb.append(getGenesisParamPrefix(e.getGenesis()));
				sb.append(" (").append(ep.getName()).append(')');

				//!: add entry parameter
				ep.setName(sb.toString());
				params.add(ep);
			}

			//~: add genesis parameters
			addNestedParameters(params, e.getGenesis());
		}
	}


	/* public: DaysGenDisp (bean) interface */

	public Entry[]  getEntries()
	{
		return entries;
	}

	public void     setEntries(Entry[] entries)
	{
		this.entries = entries;
	}

	@Param
	public int      getObjMin()
	{
		return objMin;
	}

	public void     setObjMin(int objMin)
	{
		this.objMin = objMin;
	}

	@Param
	public int      getObjMax()
	{
		return objMax;
	}

	public void     setObjMax(int objMax)
	{
		this.objMax = objMax;
	}

	/**
	 * This flag (default is false) tells to create
	 * separated transaction for each new day. This
	 * allows to insert data for long terms without
	 * overloading the database write buffer.
	 */
	@Param
	public boolean  isDayTx()
	{
		return dayTx;
	}

	public void     setDayTx(boolean dayTx)
	{
		this.dayTx = dayTx;
	}

	public GenTest  getTest()
	{
		return test;
	}

	public void     setTest(GenTest test)
	{
		this.test = test;
	}


	/* public static: entry */

	public static class Entry
	{
		/* public: Entry (bean) interface */

		public Genesis getGenesis()
		{
			return genesis;
		}

		public void    setGenesis(Genesis genesis)
		{
			this.genesis = genesis;
		}

		@Param
		public int     getWeight()
		{
			return weight;
		}

		public void    setWeight(int weight)
		{
			this.weight = weight;
		}


		/* private: entry data */

		private Genesis genesis;
		private int     weight;
	}


	/* protected: generation support */

	protected Date    findFirstGenDate(GenCtx ctx)
	{
		return EX.assertn(ctx.get(Date.class));
	}

	protected Date    findLastGenDate(GenCtx ctx)
	{
		return DU.cleanTime(new Date());
	}

	protected void    genObjectsTxDisp(GenCtx ctx, Date day)
	  throws GenesisError
	{
		if(isDayTx())
			genObjectsTx(ctx, day);
		else
			genObjects(ctx, day);
	}

	protected void    genObjectsTx(GenCtx ctx, Date day)
	  throws GenesisError
	{
		//!: invoke day generation
		genObjects(ctx, day);

		//~: mark that day
		for(Entry e : getEntries())
			if(e.getGenesis() instanceof DaysGenPart)
				((DaysGenPart)e.getGenesis()).markDayGenerated(ctx);
	}

	@SuppressWarnings("unchecked")
	protected void    genObjects(GenCtx ctx, Date day)
	  throws GenesisError
	{
		//~: select the generator entries
		Entry[] entries = selectEntries(ctx, genObjsNumber(ctx));
		Map     inds    = new IdentityHashMap(getEntries().length);

		//~: put initial in-day indices
		for(Entry e : getEntries())
			inds.put(e, 0);

		//c: for all the objects number of the day
		for(int ei = 0;(ei < entries.length);ei++)
		{
			//~: find present per-day index
			int i = (Integer)inds.get(entries[ei]);

			//~: and store it in the context
			ctx.set(DAYI, i + 1); //<-- starting from 1, not 0

			//~: generate the in-day time
			ctx.set(TIME, genDayTime(ctx, day, ei + 1, entries.length));

			//!: call the genesis unit
			if(callGenesis(ctx, entries[ei]))
			{
				//~: update per-day counter
				inds.put(entries[ei], i + 1);

				//~: update total counter
				if(total.containsKey(entries[ei]))
					total.put(entries[ei], 1 + total.get(entries[ei]));
				else
					total.put(entries[ei], 1);
			}

			ctx.set(TIME, null);
		}

		//~: write to the log
		logGen(ctx, day, inds);
	}

	protected boolean callGenesis(final GenCtx ctx, Entry e)
	  throws GenesisError
	{
		EX.assertn(e.getGenesis());

		//~: clone the genesis before the invocation
		final Genesis   g = e.getGenesis().clone();
		final Genesis   s = this;
		Throwable       x = null;
		final boolean[] r = new boolean[1];

		try
		{
			bean(TxBean.class).setNew().execute(() ->
			{
				//?: {there was generation for this day}
				if(g instanceof DaysGenPart)
					if(!((DaysGenPart)g).isDayClear(ctx))
						return;

				//!: call it in the context stacked
				try
				{
					g.generate(ctx.stack(s));
					r[0] = true;
				}
				catch(GenesisError er)
				{
					throw EX.wrap(er);
				}
			});
		}
		catch(Throwable x2)
		{
			x = EX.xrt(x2);
		}

		//?: {there were genesis error}
		if(x instanceof GenesisError)
			throw (GenesisError)x;
		else if(x != null)
			throw new GenesisError(this, ctx, x);

		return r[0];
	}

	protected void    logGen(GenCtx ctx, Date day, Map<Entry, Integer> inds)
	{
		if(!LU.isI(log(ctx)))
			return;

		for(Entry e : getEntries())
			if(inds.get(e) != 0)
				LU.I(log(ctx), DU.date2str(day),
				  " genesis [", e.getGenesis().getName(),
				  "] invoked ", inds.get(e), " times with ",
				  total.get(e), " total"
				);
	}

	protected Map<Entry, Integer> total = new HashMap<>();

	protected int     genObjsNumber(GenCtx ctx)
	{
		if(getObjMin() > getObjMax())
			throw EX.state();

		if(getObjMin() <= 0)
			throw EX.state();

		return getObjMin() +
		  ctx.gen().nextInt(getObjMax() - getObjMin() + 1);
	}

	/**
	 * Chooses random entries with of number given
	 * depending on their weights.
	 */
	protected Entry[] selectEntries(GenCtx ctx, int length)
	{
		Entry[] result = new Entry[length];

		//~: get total weight
		int W = 0; for(Entry e : getEntries())
			if(e.getWeight() < 0) throw EX.state();
			else W += e.getWeight();

		//~: select other entries in random
		next: for(int i = 0;(i < length);i++)
		{
			int x = 0, w = ctx.gen().nextInt(W);

			for(Entry e : getEntries())
				if((x += e.getWeight()) >= w)
				{
					result[i] = e;
					continue next;
				}

			throw EX.state();
		}

		return result;
	}

	/**
	 * Parameter {@code i} is in-day index from
	 * {@code 1} to {@code max} inclusive.
	 */
	protected Date    genDayTime(GenCtx ctx, Date day, int i, int max)
	{
		Calendar cl = Calendar.getInstance();
		int      TD = 24 * 60; //<-- minutes in a day
		int      td = ((i - 1)*TD + ctx.gen().nextInt(TD)) / max;

		cl.setTime(DU.cleanTime(day, cl));
		cl.set(Calendar.HOUR_OF_DAY, td / 60);
		cl.set(Calendar.MINUTE,      td % 60);

		return cl.getTime();
	}


	/* private: parameters of the generator */

	private Entry[] entries;
	private int     objMin;
	private int     objMax;
	private boolean dayTx;
	private GenTest test;
}