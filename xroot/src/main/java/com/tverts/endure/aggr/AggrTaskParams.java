package com.tverts.endure.aggr;

/**
 * Parameters of the aggregation tasks that related
 * to various aspects of the aggregation.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskParams
{
	/* task source information */

	/**
	 * If the source has date/time/timestamp value of
	 * the creation, or user set document date, etc.,
	 * send it by this parameter name.
	 */
	public static final String SOURCE_TIME =
	  "com.tverts.aggr: source: timespamp";
}