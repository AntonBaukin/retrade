package com.tverts.endure.aggr.volume;

/* Java */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTaskBase;


/**
 * Send this aggregation task to add volume to
 * aggregated value with items of class
 * {@link AggrItemVolume}.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskVolumeCreate extends AggrTaskBase
{
	/* Aggregation Task Volume Create */

	/**
	 * Positive component of the value of
	 * the aggregation item.
	 */
	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	private BigDecimal volumePositive;

	public void setVolumePositive(BigDecimal volumePositive)
	{
		this.volumePositive = volumePositive;
	}

	/**
	 * Negative component of the value of
	 * the aggregation item. Note that the
	 * value itself is not negative!
	 */
	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	private BigDecimal volumeNegative;

	public void setVolumeNegative(BigDecimal volumeNegative)
	{
		this.volumeNegative = volumeNegative;
	}

	/**
	 * Positive component of the value of
	 * the aggregation item being historical
	 * as it overwrites the preceeding items.
	 */
	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	private BigDecimal aggrPositive;

	public void setAggrPositive(BigDecimal aggrPositive)
	{
		this.aggrPositive = aggrPositive;
	}

	/**
	 * Negative component of the value of
	 * the historical aggregation item.
	 */
	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	private BigDecimal aggrNegative;

	public void setAggrNegative(BigDecimal aggrNegative)
	{
		this.aggrNegative = aggrNegative;
	}

	/**
	 * Fixes the given aggregated volumes as the value
	 * of the aggregation. Preceding aggregation items
	 * has no effect.
	 *
	 * Fixed task must have {@link #getAggrPositive()}
	 * and-or {@link #getAggrNegative()} defined, and
	 * undefined {@link #getVolumePositive()} and
	 * {@link #getVolumeNegative()}.
	 *
	 * @see  {@link AggrItemVolume#isAggrFixed()}.
	 */
	public boolean isAggrFixed()
	{
		return aggrFixed;
	}

	private boolean aggrFixed;

	public void setAggrFixed(boolean aggrFixed)
	{
		this.aggrFixed = aggrFixed;
	}
}