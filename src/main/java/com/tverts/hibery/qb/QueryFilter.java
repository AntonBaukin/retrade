package com.tverts.hibery.qb;

/**
 * Callback to update {@link QueryBuilder},
 * specially to add the filters.
 *
 * @author anton.baukin@gmail.com
 */
public interface QueryFilter
{
	/* public: QueryFilter interface */

	public void addQueryFilters(QueryBuilder query);
}