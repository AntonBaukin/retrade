package com.tverts.aggr;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorBase.AggrStruct;


/**
 * Error specific for {@link Aggregator} invocation.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrJobError extends RuntimeException
{
	public AggrJobError(AggrJob aggrJob)
	{
		this.aggrJob = aggrJob;
		this.aggrStruct = null;
	}

	public AggrJobError(Throwable cause, AggrStruct aggrStruct)
	{
		super(cause);
		this.aggrJob    = aggrStruct.job;
		this.aggrStruct = aggrStruct;
	}


	/* Aggregation Job Error */

	public AggrJob    getAggrJob()
	{
		return aggrJob;
	}

	protected final AggrJob aggrJob;

	public AggrStruct getAggrStruct()
	{
		return aggrStruct;
	}

	protected final AggrStruct aggrStruct;
}