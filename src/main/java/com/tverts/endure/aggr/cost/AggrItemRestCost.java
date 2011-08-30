package com.tverts.endure.aggr.cost;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrItemBase;


/**
 * Component of aggregated value of the Rest Cost.
 *
 * TODO complete comments on AggrItemRestCost
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

	public BigDecimal getAggrVolume()
	{
		return aggrVolume;
	}

	public void       setAggrVolume(BigDecimal v)
	{
		this.aggrVolume = v;
	}

	public BigDecimal getAggrCost()
	{
		return aggrCost;
	}

	public void       setAggrCost(BigDecimal v)
	{
		this.aggrCost = v;
	}


	/* persisted attributes: component data  */

	private BigDecimal goodVolume;
	private BigDecimal volumeCost;


	/* persisted attributes: aggregated values */

	private BigDecimal aggrVolume;
	private BigDecimal aggrCost;
}