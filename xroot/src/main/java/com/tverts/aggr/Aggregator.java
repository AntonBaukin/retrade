package com.tverts.aggr;

/**
 * Aggregation strategy that handles aggregation
 * requests collected into the jobs.
 *
 * @author anton.baukin@gmail.com
 */
public interface Aggregator
{
	/* public: Aggregator interface */

	public void aggregate(AggrJob job);
}