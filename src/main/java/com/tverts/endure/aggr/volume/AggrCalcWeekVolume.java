package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.util.Calendar;

/* com.tverts: aggregation (core) */

import com.tverts.aggr.AggregatorBase.AggrStruct;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Groups {@link AggrItemVolume} items by weeks.
 *
 * Note that a week may overlap the year border!
 * (The length of each week is 7 days.)
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   AggrCalcWeekVolume
       extends AggrCalcDatePeriodVolumeBase
{
	protected int calcYear(AggrStruct struct)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(DU.weekMonday(sourceTime(struct)));

		return cl.get(Calendar.YEAR);
	}

	protected int calcDay(AggrStruct struct)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(DU.weekMonday(sourceTime(struct)));

		return cl.get(Calendar.DAY_OF_YEAR) - 1;
	}
}