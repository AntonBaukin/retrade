package com.tverts.api.retrade.goods;

/* Java */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A unit of measure.
 */
@XmlRootElement(name = "measure")
@XmlType(name = "measure", propOrder = {
  "classCode", "classUnit", "fractional"
})
public class Measure extends CatItem
{
	@XmlElement(name = "class-code")
	public String getClassCode()
	{
		return classCode;
	}

	private String classCode;

	public void setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	@XmlElement(name = "class-unit")
	public BigDecimal getClassUnit()
	{
		return classUnit;
	}

	private BigDecimal classUnit;

	public void setClassUnit(BigDecimal classUnit)
	{
		this.classUnit = classUnit;
	}

	/**
	 * Tells that the volume measured by
	 * this unit contains fractional part.
	 */
	@XmlElement(name = "fractional")
	public boolean isFractional()
	{
		return fractional;
	}

	private boolean fractional;

	public void setFractional(boolean fractional)
	{
		this.fractional = fractional;
	}
}