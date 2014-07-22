package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.XKeyPair;


/**
 * A Good Unit. Each Good Unit is a good
 * coupled with the volume unit (mass,
 * volume, etc.). The price of the good
 * means the price of that unit.
 */
@XmlRootElement(name = "good")
@XmlType(name = "good", propOrder = {
  "measure", "XMeasure", "calc", "XCalc"
})
public class Good extends CatItem
{
	/**
	 * The primary key of the Measure Unit.
	 */
	@XKeyPair(type = Measure.class)
	@XmlElement(name = "measure")
	public Long getMeasure()
	{
		return (measure == 0L)?(null):(measure);
	}

	private long measure;

	public void setMeasure(Long measure)
	{
		this.measure = (measure == null)?(0L):(measure);
	}

	@XmlElement(name = "xmeasure")
	public String getXMeasure()
	{
		return xmeasure;
	}

	private String xmeasure;

	public void setXMeasure(String xmeasure)
	{
		this.xmeasure = xmeasure;
	}

	@XKeyPair(type = Calc.class)
	@XmlElement(name = "calc")
	public Long getCalc()
	{
		return (calc == 0L)?(null):(calc);
	}

	private long calc;

	public void setCalc(Long calc)
	{
		this.calc = (calc == null)?(0L):(calc);
	}

	@XmlElement(name = "xcalc")
	public String getXCalc()
	{
		return xcalc;
	}

	private String xcalc;

	public void setXCalc(String XCalc)
	{
		this.xcalc = XCalc;
	}
}