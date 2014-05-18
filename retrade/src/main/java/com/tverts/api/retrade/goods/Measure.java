package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A unit of measure.
 */
@XmlType(name = "measure", propOrder = {
  "classCode", "classUnit", "fractional"
})
public class Measure extends CatItem
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "class-code")
	public String getClassCode()
	{
		return classCode;
	}

	public void setClassCode(String classCode)
	{
		this.classCode = classCode;
	}

	@XmlElement(name = "class-unit")
	public BigDecimal getClassUnit()
	{
		return classUnit;
	}

	public void setClassUnit(BigDecimal classUnit)
	{
		this.classUnit = classUnit;
	}

	@XmlElement(name = "fractional")
	public Boolean isFractional()
	{
		return fractional?(Boolean.TRUE):(null);
	}

	public void setFractional(Boolean fractional)
	{
		this.fractional = Boolean.TRUE.equals(fractional);
	}


	/* attributes */

	private String     classCode;
	private BigDecimal classUnit;
	private boolean    fractional;
}