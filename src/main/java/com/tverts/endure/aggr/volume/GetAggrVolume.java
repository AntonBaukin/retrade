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
	public List<MonthVolumeCalcItem> getMonthVolumeCalcItems
	  (AggrValue aggrValue, UnityType calcType)
	{

/*

select ci from MonthVolumeCalcItem ci join ci.aggrCalc ac where
  (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType)
order by ci.year, ci.month

*/
		return (List<MonthVolumeCalcItem>) Q(

"select ci from MonthVolumeCalcItem ci join ci.aggrCalc ac where\n" +
"  (ac.aggrValue = :aggrValue) and (ac.unity.unityType = :calcType)\n" +
"order by ci.year, ci.month"

		).
		  setParameter("aggrValue", aggrValue).
		  setParameter("calcType",  calcType).
		  list();
	}


	public List<MonthVolumeCalcItem> getMonthVolumeCalcItems(AggrValue aggrValue)
	{
		return getMonthVolumeCalcItems(aggrValue, UnityTypes.unityType(
		  AggrCalc.class, AggrCalcs.AGGR_CALC_VOL_MONTH));
	}
}