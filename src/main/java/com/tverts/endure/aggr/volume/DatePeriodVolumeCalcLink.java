package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure core */

import com.tverts.endure.NumericBase;


/**
 * Reference items between {@link DatePeriodVolumeCalcItem}
 * (1, the reference owner) and {@link AggrItemVolume}
 * (N, the linked end).
 *
 * Note that this is not a real link to {@link AggrItemVolume},
 * but a copy of it's primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DatePeriodVolumeCalcLink extends NumericBase
{
	/* public: MonthVolumeCalcLink (bean) interface */

	public DatePeriodVolumeCalcItem getCalcItem()
	{
		return calcItem;
	}

	public void setCalcItem(DatePeriodVolumeCalcItem calcItem)
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

	private DatePeriodVolumeCalcItem calcItem;
	private Long                     aggrItem;

	private BigDecimal               volumePositive;
	private BigDecimal               volumeNegative;
}