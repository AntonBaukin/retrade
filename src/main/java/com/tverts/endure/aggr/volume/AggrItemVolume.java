package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

import com.tverts.endure.aggr.AggrItemBase;


/**
 * This item of aggregated value is for those aggregated
 * values that are calculated as a sum of it's items.
 * The main example is a good volume.
 *
 * The value of this component is dual. It has positive
 * and negative components that may be defined or not.
 *
 * If this component is historical, it contains a
 * dual value of the aggregation. Note that it has
 * no denominator.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrItemVolume extends AggrItemBase
{
	/* public: AggrItemVolume bean interface */

	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	public void       setVolumePositive(BigDecimal v)
	{
		this.volumePositive = v;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void       setVolumeNegative(BigDecimal v)
	{
		this.volumeNegative = v;
	}

	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	public void       setAggrPositive(BigDecimal v)
	{
		this.aggrPositive = v;
	}

	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	public void       setAggrNegative(BigDecimal v)
	{
		this.aggrNegative = v;
	}


	/* persisted attributes: the volumes */

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;

	private BigDecimal aggrPositive;
	private BigDecimal aggrNegative;
}