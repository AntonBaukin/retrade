package com.tverts.retrade.domain.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: api */

import com.tverts.api.core.Value;

/* com.tverts: endure (core) */

import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.fmt.SelfFmt;


/**
 * Ox-object that is saved as a Selection Set
 * item to filter Good Units.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "goods-filter")
@XmlType(name = "goods-filter", propOrder = {
  "attrType", "or", "min", "max"
})
public class GoodsFilter implements SelfFmt
{
	/**
	 * Primary key of Good Unit attribute type.
	 */
	@XmlElement(name = "good-attr-type")
	public Long getAttrType()
	{
		return attrType;
	}

	private Long attrType;

	public void setAttrType(Long attrType)
	{
		this.attrType = attrType;
	}

	/**
	 * Denotes an OR-selector (true) or AND-one (false).
	 */
	@XmlElement(name = "is-or")
	public boolean isOr()
	{
		return or;
	}

	private boolean or;

	public void setOr(boolean or)
	{
		this.or = or;
	}

	/**
	 * Value for equality tests, or minimum value
	 * (if defined) for ranged requests.
	 */
	@XmlElement(name = "min-value")
	public Value getMin()
	{
		return min;
	}

	private Value min;

	public void setMin(Value min)
	{
		this.min = min;
	}

	/**
	 * Maximum value of ranged requests.
	 */
	@XmlElement(name = "max-value")
	public Value getMax()
	{
		return max;
	}

	private Value max;

	public void setMax(Value max)
	{
		this.max = max;
	}


	/* Self Formatting */

	public String toString()
	{
		StringBuilder  s = new StringBuilder(92);
		AttrType      at = bean(GetUnity.class).
		  getAttrType(attrType);

		s.append("Фильтр товара (").append((or)?("ИЛИ)"):("И)"));

		if(at != null) s.append(" по атр. '").
		  append((at.getNameLo() != null)?(at.getNameLo()):(at.getName())).
		  append("'");

		Object m = (min == null)?(null):(min.value());
		Object M = (max == null)?(null):(max.value());
		EX.assertn(m);

		s.append((M != null)?(": от ["):("равно [")).append(m);
		if(M != null) s.append("] до [").append(M);

		return s.append(']').toString();
	}
}