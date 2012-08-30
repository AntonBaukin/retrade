package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.util.Date;

/* com.tverts: aggregation (core + calculations) */

import com.tverts.aggr.AggregatorBase.AggrStruct;
import com.tverts.aggr.calc.AggrCalcBase;
import com.tverts.endure.aggr.calc.AggrCalcs;


/**
 * Calculator on {@link AggrItemVolume} items.
 * Takes the source timestamp and selects
 * the month of the year. Aggregates the volumes
 * grouped by the months.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrCalcMonthVolume extends AggrCalcBase
{
	/* protected: AggrCalcBase interface */

	protected void calc(AggrStruct struct)
	{
		if(aggrTask(struct) instanceof AggrTaskVolumeCreate)
			if(!struct.items().isEmpty())
				calcCreate(struct);

		if(aggrTask(struct) instanceof AggrTaskVolumeDelete)
			if(!struct.items().isEmpty())
				calcDelete(struct);
	}


	/* protected: calculations */

	protected void calcCreate(AggrStruct struct)
	{
		//?: {the source time is not provided}
		if(sourceTime(struct) == null)
			return;
	}

	protected void calcDelete(AggrStruct struct)
	{

	}


	/* protected: support routines */

	protected Date sourceTime(AggrStruct struct)
	{
		return param(struct, AggrCalcs.PARAM_SOURCE_TIME, Date.class);
	}
}