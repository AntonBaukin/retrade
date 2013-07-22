package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTaskBase;


/**
 * Send this aggregation task to add volume to the
 * aggregated value with items of class {@link AggrItemVolume}.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrTaskVolumeCreate extends AggrTaskBase
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTaskVolumeCreate (bean) interface */

	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	public void       setVolumePositive(BigDecimal volumePositive)
	{
		this.volumePositive = volumePositive;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void       setVolumeNegative(BigDecimal volumeNegative)
	{
		this.volumeNegative = volumeNegative;
	}

	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	public void       setAggrPositive(BigDecimal aggrPositive)
	{
		this.aggrPositive = aggrPositive;
	}

	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	public void       setAggrNegative(BigDecimal aggrNegative)
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
	public boolean    isAggrFixed()
	{
		return aggrFixed;
	}

	public void       setAggrFixed(boolean aggrFixed)
	{
		this.aggrFixed = aggrFixed;
	}


	/* private: the volumes */

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;
	private BigDecimal aggrPositive;
	private BigDecimal aggrNegative;
	private boolean    aggrFixed;
}