package com.tverts.hibery.qb;

/**
 * A strategy of updating Query Builder
 * with adding restrictions and so.
 *
 * In the most cases a strategy must be
 * a serializable Java Bean, but this
 * is not required in the interface.
 *
 *
 * @author anton.baukin@gmail.com.
 */
public interface TuneQuery
{
	/* public: Tune Query*/

	public void tuneQuery(QueryBuilder qb);
}