package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure core */

import com.tverts.endure.NumericBase;


/**
 * Reference items between {@link MonthVolumeCalcItem}
 * (1, the reference owner) and {@link AggrItemVolume}
 * (N, the linked end).
 *
 * @author anton.baukin@gmail.com
 */
public class MonthVolumeCalcLink extends NumericBase
{
	/* public: MonthVolumeCalcLink (bean) interface */

	public MonthVolumeCalcItem getCalcItem()
	{
		return calcItem;
	}

	public void setCalcItem(MonthVolumeCalcItem calcItem)
	{
		this.calcItem = calcItem;
	}

	public Long getAggrItem()
	{
		return aggrItem;
	}

	public void setAggrItem(Long aggrItem)
	{
		this.aggrItem = aggrItem;
	}

	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	public void setVolumePositive(BigDecimal volumePositive)
	{
		this.volumePositive = volumePositive;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void setVolumeNegative(BigDecimal volumeNegative)
	{
		this.volumeNegative = volumeNegative;
	}


	/* persisted attributes */

	private MonthVolumeCalcItem calcItem;
	private Long                aggrItem;

	private BigDecimal          volumePositive;
	private BigDecimal          volumeNegative;
}