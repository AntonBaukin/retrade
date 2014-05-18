package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.XKeyPair;


/**
 * A good unit to sell. Each good unit is a good
 * coupled with the volume unit (mass, volume, etc.).
 * The price of the good means the price of that unit.
 */
@XmlType(name = "good", propOrder = {
  "measure", "XMeasure"
})
public class Good extends CatItem
{
	public static final long serialVersionUID = 0L;


	/**
	 * The primary key of the Measure Unit.
	 */
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


	/* attributes */

	private long   measure;
	private String xmeasure;
}