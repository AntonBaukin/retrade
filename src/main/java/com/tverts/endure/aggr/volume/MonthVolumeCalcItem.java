package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.calc.AggrCalcItemBase;


/**
 * Aggregation calculation over the Volume Aggregated
 * Values that summarises items of the value grouped
 * by (year, month).
 *
 * @author anton.baukin@gmail.com
 */
public class MonthVolumeCalcItem extends AggrCalcItemBase
{
	/* public: MonthVolumeCalcItem (bean) interface */

	public int        getYear()
	{
		return year;
	}

	public void       setYear(int year)
	{
		this.year = year;
	}

	/**
	 * The month within the year. Starts from 0 as in Calendar.
	 */
	public int        getMonth()
	{
		return month;
	}

	public void       setMonth(int month)
	{
		this.month = month;
	}

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


	/* persisted attributes */

	private int        year;
	private int        month;

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;
}