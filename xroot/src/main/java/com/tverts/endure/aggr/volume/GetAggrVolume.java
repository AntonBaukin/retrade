package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.util.List;

/* Spring Framework */

import org.springframework.stereotype.Component;

/* com.tverts: hibery */

import com.tverts.hibery.GetObjectBase;

/* com.tverts: endure (core + aggregation) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.aggr.calc.AggrCalc;
import com.tverts.endure.aggr.calc.AggrCalcs;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Loads object specific to Aggregated Values with
 * {@link AggrItemVolume} items.
 *
 *
 * @author anton.baukin@gmail.com
 */
@Component("getAggrVolume")
public class GetAggrVolume extends GetObjectBase
{
	/* Calculations of Monthly Volumes */

	/**
	 * Returns all the items ordered by (year, month).
	 */
	@SuppressWarnings("unchecked")
	public List<DatePeriodVolumeCalcItem> getDatePeriodVolumeCalcItems
	  (AggrValue aggrValue, UnityType calcType)
	{

/*

 select ci from DatePeriodVolumeCalcItem ci join ci.aggrCalc ac where
   (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType)
 order by ci.aggrCalc.id, ci.year, ci.day

*/

		return (List<DatePeriodVolumeCalcItem>) Q(

"select ci from DatePeriodVolumeCalcItem ci join ci.aggrCalc ac where\n" +
"  (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType)\n" +
"order by ci.aggrCalc.id, ci.year, ci.day"

		).
		  setParameter("aggrValue", aggrValue).
		  setParameter("calcType",  calcType).
		  list();
	}

	public DatePeriodVolumeCalcItem getDatePeriodVolumeCalcItem
	  (AggrValue aggrValue, UnityType calcType, int year, int day)
	{

/*

 select ci from DatePeriodVolumeCalcItem ci join ci.aggrCalc ac where
   (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType) and
   (ci.year = :year) and (ci.day = :day)

*/
		return (DatePeriodVolumeCalcItem) Q(

"select ci from DatePeriodVolumeCalcItem ci join ci.aggrCalc ac where\n" +
"  (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType) and\n" +
"  (ci.year = :year) and (ci.day = :day)"

		).
		  setParameter("aggrValue", aggrValue).
		  setParameter("calcType",  calcType).
		  setInteger  ("year",      year).
		  setInteger  ("day",       day).
		  uniqueResult();
	}

	public List<DatePeriodVolumeCalcItem> getMonthVolumeCalcItems(AggrValue aggrValue)
	{
		return getDatePeriodVolumeCalcItems(aggrValue, UnityTypes.unityType(
		  AggrCalc.class, AggrCalcs.AGGR_CALC_VOL_MONTH));
	}

	public DatePeriodVolumeCalcItem getMonthVolumeCalcItem
	  (AggrValue aggrValue, int year, int month)
	{
		return getDatePeriodVolumeCalcItem(aggrValue, UnityTypes.unityType(
		  AggrCalc.class, AggrCalcs.AGGR_CALC_VOL_MONTH), year, DU.monthDay(year, month));
	}

	public List<DatePeriodVolumeCalcItem> getWeekVolumeCalcItems(AggrValue aggrValue)
	{
		return getDatePeriodVolumeCalcItems(aggrValue, UnityTypes.unityType(
		  AggrCalc.class, AggrCalcs.AGGR_CALC_VOL_WEEK));
	}
}