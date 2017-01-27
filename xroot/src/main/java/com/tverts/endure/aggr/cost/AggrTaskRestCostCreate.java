package com.tverts.endure.aggr.cost;

/* Java */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTaskBase;


/**
 * Send this aggregation task to add volume and it's cost
 * to the aggregated value with items of class
 * {@link AggrItemRestCost}.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskRestCostCreate extends AggrTaskBase
{
	/* Aggregation Task Rest Cost Create */

	/**
	 * The volume bought or sold. The value is positive
	 * for buy operations, and negative for sells.
	 * (Sells do not change the rest cost value.)
	 */
	public BigDecimal getGoodVolume()
	{
		return goodVolume;
	}

	private BigDecimal goodVolume;

	public void setGoodVolume(BigDecimal goodVolume)
	{
		this.goodVolume = goodVolume;
	}

	/**
	 * The cost of the volume. Is always positive.
	 */
	public BigDecimal getVolumeCost()
	{
		return volumeCost;
	}

	private BigDecimal volumeCost;

	public void setVolumeCost(BigDecimal volumeCost)
	{
		this.volumeCost = volumeCost;
	}
}