package com.tverts.endure.aggr.calc;

/**
 * Collection of constants related to implemented
 * Aggregation Calculations.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrCalcs
{
	/* Calculation Types */

	/**
	 * Calculation over the aggregated volume value.
	 * Groups the volume items by the months and summarises them.
	 */
	public static final String AGGR_CALC_VOL_MONTH =
	  "Aggr Calc: Volume: Monthly";



	/* Shared Parameters */

	/**
	 * Timestamp of the aggregation source object.
	 * Used by date range calculators.
	 */
	public static final String PARAM_SOURCE_TIME =
	  AggrCalcs.class.getName() + ": param: source timestamp";
}