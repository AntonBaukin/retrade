package com.tverts.hibery.qb;

/* Java */

import java.io.Serializable;


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
public interface TuneQuery extends Serializable
{
	/* Tune Query */

	public void tuneQuery(QueryBuilder qb);
}