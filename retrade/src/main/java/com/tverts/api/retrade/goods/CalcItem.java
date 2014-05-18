package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;


/**
 * Calculation item.
 */
@XmlType(name = "calc-part", propOrder = {
  "good", "XGood", "measure", "XMeasure", "volume", "amount"
})
public class CalcItem implements Serializable
{
	public static final long serialVersionUID = 0L;


	@XKeyPair(type = Good.class)
	@XmlElement(name = "good")
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	@XKeyPair(type = Measure.class)
	@XmlElement(name = "measure")
	public Long getMeasure()
	{
		return (measure == 0L)?(null):(measure);
	}

	public void setMeasure(Long measure)
	{
		this.measure = (measure == null)?(0L):(measure);
	}

	@XmlElement(name = "xmeasure")
	public String getXMeasure()
	{
		return xmeasure;
	}

	public void setXMeasure(String xmeasure)
	{
		this.xmeasure = xmeasure;
	}

	@XmlElement(name = "volume")
	public BigDecimal getVolume()
	{
		return volume;
	}

	public void setVolume(BigDecimal volume)
	{
		this.volume = volume;
	}

	@XmlElement(name = "amount")
	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}


	/* attributes */

	private long       good;
	private String     xgood;
	private long       measure;
	private String     xmeasure;
	private BigDecimal volume;
	private BigDecimal amount;
}