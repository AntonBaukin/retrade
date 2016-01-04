package com.tverts.api.retrade.goods;

/* Java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.Value;
import com.tverts.api.core.XKeyPair;
import com.tverts.api.support.EX;


/**
 * A Good Unit. Each Good Unit is a good
 * coupled with the volume unit (mass,
 * volume, etc.). The price of the good
 * means the price of that unit.
 */
@XmlRootElement(name = "good")
@XmlType(name = "good", propOrder = {
  "measure", "XMeasure", "calc", "XCalc",
  "visibleSell", "visibleBuy",
  "visibleLists", "visibleReports",
  "group", "attrValues"
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

	@XmlTransient
	public Map<String, Object> getAttrs()
	{
		return attributes;
	}

	private Map<String, Object> attributes;

	public void setAttrs(Map<String, Object> attributes)
	{
		this.attributes = attributes;
	}

	@XmlElement(name = "good-attr")
	@XmlElementWrapper(name = "attributes")
	public List<GoodAttr> getAttrValues()
	{
		return convert(attributes);
	}

	public void setAttrValues(List<GoodAttr> ats)
	{
		this.attributes = convert(ats);
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


	/* Support Routines */

	@SuppressWarnings("unchecked")
	public static List<GoodAttr> convert(Map<String, Object> ats)
	{
		if((ats == null) || ats.isEmpty())
			return null;

		List<GoodAttr> res = new ArrayList<>(ats.size());
		for(Map.Entry<String, Object> e : ats.entrySet())
		{
			GoodAttr a = new GoodAttr();
			res.add(a);

			a.setName(e.getKey());

			Object   v = e.getValue();
			if(v instanceof Object[])
				v = Arrays.asList((Object[]) v);

			if(!(v instanceof List))
				a.setValue(new Value().value(e.getValue()));
			else
			{
				ArrayList<Value> vs = new ArrayList<>();
				for(Object x : (List)v)
					vs.add(new Value().value(x));

				a.setValues(vs);
			}
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> convert(List<GoodAttr> ats)
	{
		if((ats == null) || ats.isEmpty())
			return null;

		Map<String, Object> res = new LinkedHashMap<>(ats.size());
		for(GoodAttr a : ats)
			if(a.getValues() == null)
				res.put(EX.asserts(a.getName()), EX.assertn(a.getValue()).value());
			else
			{
				ArrayList vs = new ArrayList();
				for(Value v : a.getValues())
					vs.add(v.value());

				res.put(EX.asserts(a.getName()), vs);
			}

		return res;
	}
}