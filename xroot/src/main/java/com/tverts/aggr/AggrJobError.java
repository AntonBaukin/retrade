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
	/* public: constructors */

	public AggrJobError(AggrJob aggrJob)
	{
		this.aggrJob = aggrJob;
		this.aggrStruct = null;
	}

	public AggrJobError(AggrStruct aggrStruct)
	{
		this.aggrJob    = aggrStruct.job;
		this.aggrStruct = aggrStruct;
	}

	public AggrJobError(Throwable cause, AggrJob aggrJob)
	{
		super(cause);
		this.aggrJob    = aggrJob;
		this.aggrStruct = null;
	}

	public AggrJobError(Throwable cause, AggrStruct aggrStruct)
	{
		super(cause);
		this.aggrJob    = aggrStruct.job;
		this.aggrStruct = aggrStruct;
	}


	/* public: AggrJobError interface */

	public AggrJob    getAggrJob()
	{
		return aggrJob;
	}

	public AggrStruct getAggrStruct()
	{
		return aggrStruct;
	}


	/* protected: the aggregation job */

	protected transient final AggrJob    aggrJob;
	protected transient final AggrStruct aggrStruct;
}