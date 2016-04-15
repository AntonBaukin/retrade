package com.tverts.endure.aggr.volume;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: endure (aggregation calculations) */

import com.tverts.endure.aggr.calc.AggrCalcItemBase;

/* com.tverts: support */

import com.tverts.support.DU;
import com.tverts.support.jaxb.DateAdapter;


/**
 * View of Aggregation Volume items and the
 * items of calculations.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "aggr-volume")
public class AggrVolumeView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getDateMonthYear()
	{
		return dateMonthYear;
	}

	public void setDateMonthYear(String dateMonthYear)
	{
		this.dateMonthYear = dateMonthYear;
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

	@XmlElement
	public BigDecimal getVolumeBalance()
	{
		BigDecimal r = getVolumePositive();

		if(r == null) r = BigDecimal.ZERO;
		if(getVolumeNegative() != null)
			r = r.subtract(getVolumeNegative());

		return r;
	}


	/* public: init interface */

	public AggrVolumeView init(Object o)
	{
		if(o instanceof AggrCalcItemBase)
			objectKey = ((AggrCalcItemBase)o).getPrimaryKey();

		if(o instanceof DatePeriodVolumeCalcItem)
			return this.init((DatePeriodVolumeCalcItem) o);

		return this;
	}

	public AggrVolumeView init(DatePeriodVolumeCalcItem ci)
	{
		date           = ci.dayDate();
		dateMonthYear  = DU.monthAbbr(date);

		volumePositive = ci.getVolumePositive();
		volumeNegative = ci.getVolumeNegative();

		return this;
	}


	/* attributes of the view */

	private Long       objectKey;
	private Date       date;
	private String     dateMonthYear;
	private BigDecimal volumePositive;
	private BigDecimal volumeNegative;
}