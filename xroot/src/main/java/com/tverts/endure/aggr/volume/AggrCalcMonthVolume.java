package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.util.Calendar;

/* com.tverts: aggregation (core) */

import com.tverts.aggr.AggregatorBase.AggrStruct;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Groups {@link AggrItemVolume} items by months.
 *
 * @author anton.baukin@gmail.com
 */
public class   AggrCalcMonthVolume
       extends AggrCalcDatePeriodVolumeBase
{
	/* protected: AggrCalcDatePeriodVolumeBase interface */

	protected int calcYear(AggrStruct struct)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(sourceTime(struct));

		return cl.get(Calendar.YEAR);
	}

	protected int calcDay(AggrStruct struct)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(sourceTime(struct));

		return DU.monthDay(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH));
	}

	protected int calcLength(AggrStruct struct)
	{
		return DU.monthLength(sourceTime(struct));
	}
}