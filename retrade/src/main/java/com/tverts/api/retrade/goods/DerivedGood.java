package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeysAlias;


/**
 * Defines a Good Unit that is produced
 * based on else good.
 */
@XKeysAlias(Good.class)
@XmlType(name = "derived-good", propOrder = {
  "superGood", "subCode", "subVolume", "semiReady"
})
public class DerivedGood extends Good
{
	public static final long serialVersionUID = 0L;

	@XmlElement(name = "super-good")
	public Long getSuperGood()
	{
		return (superGood == 0L)?(null):(superGood);
	}

	public void setSuperGood(Long superGood)
	{
		this.superGood = (superGood == null)?(0L):(superGood);
	}

	@XmlElement(name = "sub-code")
	public String getSubCode()
	{
		return subCode;
	}

	public void setSubCode(String subCode)
	{
		this.subCode = subCode;
	}

	@XmlElement(name = "sub-volume")
	public BigDecimal getSubVolume()
	{
		return subVolume;
	}

	public void setSubVolume(BigDecimal subVolume)
	{
		this.subVolume = subVolume;
	}

	public boolean isSemiReady()
	{
		return semiReady;
	}

	public void setSemiReady(boolean semiReady)
	{
		this.semiReady = semiReady;
	}


	/* attributes */

	private long       superGood;
	private String     subCode;
	private BigDecimal subVolume;
	private boolean    semiReady;
}