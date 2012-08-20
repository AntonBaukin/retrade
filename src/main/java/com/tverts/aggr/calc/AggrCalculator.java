package com.tverts.aggr.calc;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorBase;
import com.tverts.aggr.AggregatorBase.AggrStruct;


/**
 * This interface is specific to {@link AggregatorBase}
 * implementation.
 *
 * When the aggregation of the task within the job is
 * done, the base implementation takes all the calculation
 * instances related with the aggregated value, and invokes
 * the calculators able to process them.
 *
 * @author anton.baukin@gmail.com
 */
public interface AggrCalculator
{
	/* public: AggrCalculator interface */

	public void calculate(AggrStruct struct);
}