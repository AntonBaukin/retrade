package com.tverts.endure.aggr.cost;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTaskBase;


/**
 * Send this aggregation task to add volume and it's cost
 * to the aggregated value with items of class
 * {@link AggrItemRestCost}.
 *
 * Tasks having positive value correspond to buy operations.
 * Each such a component is set internally to be historical.
 *
 * Sell operations (with negative volume) do not change the
 * rest cost value.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskRestCostCreate extends AggrTaskBase
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTaskRestCostCreate (bean) interface */

	public BigDecimal getGoodVolume()
	{
		return goodVolume;
	}

	public void       setGoodVolume(BigDecimal goodVolume)
	{
		this.goodVolume = goodVolume;
	}

	public BigDecimal getVolumeCost()
	{
		return volumeCost;
	}

	public void       setVolumeCost(BigDecimal volumeCost)
	{
		this.volumeCost = volumeCost;
	}


	/* private: the volume and it's cost */

	private BigDecimal goodVolume;
	private BigDecimal volumeCost;
}