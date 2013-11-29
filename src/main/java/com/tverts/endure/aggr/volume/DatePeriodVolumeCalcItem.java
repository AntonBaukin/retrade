package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.calc.AggrCalcItemBase;

/* com.tverts: support */

import com.tverts.support.DU;


/**
 * Aggregation calculation over the Volume Aggregated Values.
 *
 * Aggregation calculation strategy defines the period of
 * grouping: for example, a month, or a week. The length
 * of the period (in days) may wary, as for months.
 *
 * The item is uniquely defined by the pair (year, day),
 * the length of the grouping is a hint.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class DatePeriodVolumeCalcItem extends AggrCalcItemBase
{
	/* public: bean interface */

	public int        getYear()
	{
		return year;
	}

	public void       setYear(int year)
	{
		this.year = year;
	}

	/**
	 * The day of the period start within the year.
	 * Day 0 means January, 1st.
	 */
	public int        getDay()
	{
		return day;
	}

	public void       setDay(int day)
	{
		this.day = day;
	}

	public Date       dayDate()
	{
		Calendar cl = Calendar.getInstance();
		DU.cleanTime(cl);

		cl.set(Calendar.YEAR, year);
		cl.set(Calendar.DAY_OF_YEAR, day + 1);

		return cl.getTime();
	}

	/**
	 * Tells the period of grouping (in days).
	 * May wary from item to item.
	 *
	 * Note that the period may overlap the year!
	 */
	public int        getLength()
	{
		return length;
	}

	public void       setLength(int length)
	{
		this.length = length;
	}

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


	/* persisted attributes */

	private int year;
	private int day;
	private int length;

	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;
}