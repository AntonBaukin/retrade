package com.tverts.api.core;

/**
 * Query to return a sub-list of ordered data.
 *
 * @author anton.baukin@gmail.com
 */
public interface LimitedQuery
{
	/* public: LimitedQuery interface */

	public Long getFirstResult();

	public void setFirstResult(Long offset);

	public Long getResultsLimit();

	public void setResultsLimit(Long limit);
}