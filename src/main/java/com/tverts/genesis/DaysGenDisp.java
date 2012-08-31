package com.tverts.genesis;

/* standard Java classes */

import java.util.Calendar;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

/* Spring framework */

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* com.tverts: support */

import com.tverts.support.DU;
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
		if(getEntries() == null) throw new IllegalStateException();
		if(getDays() <= 0) throw new IllegalStateException();

		//~: the start day (inclusive)
		Date curDay  = findFirstGenDate(ctx);
		if(curDay == null) return;

		//~: the final day (inclusive)
		Date endDay  = findLastGenDate(ctx);
		if(endDay == null) return;

		//c: generate till the last day
		while(!curDay.after(endDay))
		{
			genObjectsTxDisp(ctx, curDay);
			curDay = DU.addDaysClean(curDay, +1);
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

	public int      getDays()
	{
		return days;
	}

	public void     setDays(int days)
	{
		this.days = days;
	}

	public int      getObjMin()
	{
		return objMin;
	}

	public void     setObjMin(int objMin)
	{
		this.objMin = objMin;
	}

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
	public boolean  isDayTx()
	{
		return dayTx;
	}

	public void     setDayTx(boolean dayTx)
	{
		this.dayTx = dayTx;
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

	protected Date  findFirstGenDate(GenCtx ctx)
	{
		return DU.addDaysClean(new Date(), -getDays());
	}

	protected Date  findLastGenDate(GenCtx ctx)
	{
		return DU.cleanTime(new Date());
	}

	protected void  genObjectsTxDisp(GenCtx ctx, Date day)
	  throws GenesisError
	{
		if(isDayTx())
			genObjectsTx(ctx, day);
		else
			genObjects(ctx, day);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void  genObjectsTx(GenCtx ctx, Date day)
	  throws GenesisError
	{
		genObjects(ctx, day);
	}

	@SuppressWarnings("unchecked")
	protected void  genObjects(GenCtx ctx, Date day)
	  throws GenesisError
	{
		//~: set the day parameter of the context
		ctx.set(DAY, day);

		Map inds = new IdentityHashMap(getEntries().length);
		int objn = genObjsNumber(ctx);

		//~: put initial indices (needed for logging)
		for(Entry e : getEntries())
		   inds.put(e, 1);

		//c: for all the objects number of the day
		for(int obji = 1;(obji <= objn);obji++)
		{
			//~: select the next generator entry by it's weight
			Entry e = selectEntry(ctx);

			//~: find the present per-day index
			int   i = (Integer)inds.get(e);
			inds.put(e, i + 1);

			//~: and store it in the context
			ctx.set(DAYI, i); //<-- starting from 1, not 0

			//~: generate the in-day time
			ctx.set(TIME, genDayTime(ctx, day, obji, objn));

			//!: call the genesis unit
			callGenesis(ctx, e);
		}

		//~: write to the log
		logGen(ctx, day, inds);
	}

	protected void  callGenesis(GenCtx ctx, Entry e)
	  throws GenesisError
	{
		if(e.getGenesis() == null)
			throw new IllegalStateException();

		//~: clone the genesis before the invocation
		Genesis g = e.getGenesis().clone();

		//!: call it in the context stacked
		g.generate(ctx.stack(this));
	}

	protected void  logGen(GenCtx ctx, Date day, Map inds)
	{
		if(LU.isI(log(ctx))) for(Entry e : getEntries())
			LU.I(log(ctx), DU.date2str(day),
			     " genesis ", e.getGenesis().getName(),
			     " invoked times: ", ((Integer)inds.get(e) - 1)
			);
	}

	protected int   genObjsNumber(GenCtx ctx)
	{
		if(getObjMin() > getObjMax())
			throw new IllegalStateException();

		if(getObjMin() <= 0)
			throw new IllegalStateException();

		return getObjMin() +
		  ctx.gen().nextInt(getObjMax() - getObjMin() + 1);
	}
	
	protected Entry selectEntry(GenCtx ctx)
	{
		int b, w = 0;

		for(Entry e : getEntries())
			if(e.getWeight() <= 0) throw new IllegalStateException();
			else w += e.getWeight();

		b = ctx.gen().nextInt(w); w = 0;

		for(Entry e : getEntries())
			if((w += e.getWeight()) >= b)
				return e;

		throw new IllegalStateException();
	}

	/**
	 * Parameter {@code i} is in-day index from
	 * {@code 1} to {@code max} inclusive.
	 */
	protected Date  genDayTime(GenCtx ctx, Date day, int i, int max)
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
	private int     days;
	private int     objMin;
	private int     objMax;
	private boolean dayTx;
}