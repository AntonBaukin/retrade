package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

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
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.volumePositive = v;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void       setVolumeNegative(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.volumeNegative = v;
	}

	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	public void       setAggrPositive(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrPositive = v;
	}

	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	public void       setAggrNegative(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrNegative = v;
	}

	/**
	 * Tells that this historical value is fixed.
	 *
	 * If true, {@link #getAggrPositive()} and-or
	 * {@link #getAggrNegative()} must be set,
	 * and the aggregator must support ordering!
	 *
	 * Fixed values are never modified when previous
	 * items are inserted-updated-deleted.
	 *
	 * The aggregated value equals to the last fixed
	 * historical value with updates of all the
	 * following items.
	 */
	public boolean    isAggrFixed()
	{
		return aggrFixed;
	}

	public void       setAggrFixed(boolean aggrFixed)
	{
		this.aggrFixed = aggrFixed;
	}


	/* persisted attributes: the volumes */

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;

	private BigDecimal aggrPositive;
	private BigDecimal aggrNegative;
	private boolean    aggrFixed;
}