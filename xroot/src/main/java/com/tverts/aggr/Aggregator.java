package com.tverts.aggr;

/**
 * Aggregation strategy that handles aggregation
 * requests collected into the jobs.
 *
 * @author anton.baukin@gmail.com
 */
public interface Aggregator
{
	/* Aggregator */

	public void aggregate(AggrJob job);
}