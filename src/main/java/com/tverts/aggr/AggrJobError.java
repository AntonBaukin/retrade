package com.tverts.aggr;

/**
 * Error specific for {@link Aggregator} invocation.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrJobError extends RuntimeException
{
	/* public: constructors */

	public AggrJobError(AggrJob aggrJob)
	{
		this.aggrJob = aggrJob;
	}

	public AggrJobError(Throwable cause, AggrJob aggrJob)
	{
		super(cause);
		this.aggrJob = aggrJob;
	}


	/* public: AggrJobError interface */

	public AggrJob getAggrJob()
	{
		return aggrJob;
	}


	/* protected: the aggregation job */

	protected transient final AggrJob aggrJob;
}