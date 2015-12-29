package com.tverts.api.retrade.goods;

/* Java */

import java.math.BigDecimal;

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
  "measure", "XMeasure", "calc", "XCalc",
  "visibleSell", "visibleBuy", "visibleLists", "visibleReports",
  "group", "barCode", "netWeight", "grossWeight"
})
public class Good extends CatItem
{
	/**
	 * The primary key of the Measure Unit.
	 */
	@XKeyPair(type = Measure.class)
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

	public String getGroup()
	{
		return group;
	}

	private String group;

	public void setGroup(String group)
	{
		this.group = group;
	}

	@XmlElement(name = "bar-code")
	public String getBarCode()
	{
		return barCode;
	}

	private String barCode;

	public void setBarCode(String barCode)
	{
		this.barCode = barCode;
	}

	@XmlElement(name = "net-weight")
	public BigDecimal getNetWeight()
	{
		return netWeight;
	}

	private BigDecimal netWeight;

	public void setNetWeight(BigDecimal netWeight)
	{
		this.netWeight = netWeight;
	}

	@XmlElement(name = "gross-weight")
	public BigDecimal getGrossWeight()
	{
		return grossWeight;
	}

	private BigDecimal grossWeight;

	public void setGrossWeight(BigDecimal grossWeight)
	{
		this.grossWeight = grossWeight;
	}


	/* Visibility Flags */

	@XmlElement(name = "visible-sell")
	public boolean isVisibleSell()
	{
		return visibleSell;
	}

	private boolean visibleSell = true;

	public void setVisibleSell(boolean visibleSell)
	{
		this.visibleSell = visibleSell;
	}

	@XmlElement(name = "visible-buy")
	public boolean isVisibleBuy()
	{
		return visibleBuy;
	}

	private boolean visibleBuy = true;

	public void setVisibleBuy(boolean visibleBuy)
	{
		this.visibleBuy = visibleBuy;
	}

	@XmlElement(name = "visible-lists")
	public boolean isVisibleLists()
	{
		return visibleLists;
	}

	private boolean visibleLists = true;

	public void setVisibleLists(boolean visibleLists)
	{
		this.visibleLists = visibleLists;
	}

	@XmlElement(name = "visible-reports")
	public boolean isVisibleReports()
	{
		return visibleReports;
	}

	private boolean visibleReports = true;

	public void setVisibleReports(boolean visibleReports)
	{
		this.visibleReports = visibleReports;
	}
}