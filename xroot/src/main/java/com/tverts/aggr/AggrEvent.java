package com.tverts.aggr;

/* com.tverts: services */

import com.tverts.system.services.events.ServiceDelayedEventBase;


/**
 * Soft (not obligatory) notification for Aggregation
 * Service to schedule background aggregation on
 * the aggragation request given by the key.
 * If no request is referred checks all.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrEvent extends ServiceDelayedEventBase
{
	public static final long serialVersionUID = 0L;


	/* Aggregation Event */

	/**
	 * If set, tells the exact aggregation value
	 * to fetch for the requests.
	 */
	public Long getAggrValue()
	{
		return aggrValue;
	}

	private Long aggrValue;

	public AggrEvent setAggrValue(Long aggrValue)
	{
		this.aggrValue = aggrValue;
		return this;
	}
}