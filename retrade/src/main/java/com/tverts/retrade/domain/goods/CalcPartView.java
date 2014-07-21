package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Read and edit view on a Good Calculation Part.
 *
 * @author anton.baukin@gmail.com
 */
@XmlType(name = "calc-part")
public class CalcPartView implements Serializable
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

	public Long getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(Long goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	@XmlElement
	public String getGoodCode()
	{
		return goodCode;
	}

	@XmlElement
	public String getGoodName()
	{
		return goodName;
	}

	@XmlElement
	public Boolean isGoodSemiReady()
	{
		return goodSemiReady;
	}

	@XmlElement
	public String getMeasureName()
	{
		return measureName;
	}

	public void setMeasureName(String measureName)
	{
		this.measureName = measureName;
	}

	public boolean isInteger()
	{
		return integer;
	}

	public void setInteger(boolean integer)
	{
		this.integer = integer;

		if(this.volume != null) if(integer)
			this.volume = this.volume.setScale(0);
		else
			volume = volume.setScale(5, BigDecimal.ROUND_HALF_EVEN);
	}

	public BigDecimal getVolume()
	{
		return volume;
	}

	public void setVolume(BigDecimal volume)
	{
		if(volume != null) if(this.integer)
			volume = volume.setScale(0);
		else
			volume = volume.setScale(5, BigDecimal.ROUND_HALF_EVEN);

		this.volume = volume;
	}

	public Boolean getSemiReady()
	{
		return semiReady;
	}

	public void setSemiReady(Boolean semiReady)
	{
		this.semiReady = semiReady;
	}


	/* public: init interface */

	public CalcPartView init(Object o)
	{
		if(o instanceof CalcPart)
			return this.init((CalcPart) o);

		return this;
	}

	public CalcPartView init(CalcPart p)
	{
		this.objectKey = p.getPrimaryKey();
		setVolume(p.getVolume());
		this.semiReady = p.getSemiReady();

		return this.init(p.getGoodUnit());
	}

	public CalcPartView init(GoodUnit gu)
	{
		this.goodUnit = gu.getPrimaryKey();
		this.goodCode = gu.getCode();
		this.goodName = gu.getName();

		if(gu.getGoodCalc() != null)
			this.goodSemiReady = gu.getGoodCalc().isSemiReady();

		return this.init(gu.getMeasure());
	}

	public CalcPartView init(MeasureUnit mu)
	{
		this.measureName = mu.getCode();
		setInteger(!mu.getOx().isFractional());

		return this;
	}


	/* calc part properties */

	private Long       objectKey;
	private Long       goodUnit;
	private String     goodCode;
	private String     goodName;
	private Boolean    goodSemiReady;
	private String     measureName;
	private boolean    integer;
	private BigDecimal volume;
	private Boolean    semiReady;
}