package com.tverts.endure.aggr.cost;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItemBase;


/**
 * Component of aggregated value of the Rest Cost.
 *
 * COMMENT complete comments on AggrItemRestCost
 *
 * @author anton.baukin@gmail.com
 */
public class AggrItemRestCost extends AggrItemBase
{
	/* public: AggrItemRestCost (bean) interface */

	public BigDecimal getGoodVolume()
	{
		return goodVolume;
	}

	public void       setGoodVolume(BigDecimal v)
	{
		this.goodVolume = v;
	}

	public BigDecimal getVolumeCost()
	{
		return volumeCost;
	}

	public void       setVolumeCost(BigDecimal v)
	{
		this.volumeCost = v;
	}

	/**
	 * A 64-length string (one character for '.') to
	 * store rounded decimal value of 'Z' component.
	 * Stored in buy items.
	 */
	public String     getRestCost()
	{
		return restCost;
	}

	public void       setRestCost(String v)
	{
		this.restCost = v;
	}

	/**
	 * Aggregated volume balance of buy minus sell
	 * items. Stored in buy items.
	 */
	public BigDecimal getAggrVolume()
	{
		return aggrVolume;
	}

	public void       setAggrVolume(BigDecimal v)
	{
		this.aggrVolume = v;
	}

	/**
	 * Summary value of all sell items between the
	 * closest left and right buy items. Take volume
	 * of the left item and the delta of the right
	 * one to get the volume of the right.
	 */
	public BigDecimal getDeltaVolume()
	{
		return deltaVolume;
	}

	public void       setDeltaVolume(BigDecimal deltaVolume)
	{
		this.deltaVolume = deltaVolume;
	}

	/* persisted attributes: component data  */

	private BigDecimal goodVolume;
	private BigDecimal volumeCost;


	/* persisted attributes: aggregated values */

	private String     restCost;
	private BigDecimal aggrVolume;
	private BigDecimal deltaVolume;
}