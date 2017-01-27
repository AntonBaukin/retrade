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

import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.fmt.SelfFmt;


/**
 * Ox-object that is saved as a Selection Set
 * item to filter Good Units.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "goods-filter")
@XmlType(name = "goods-filter", propOrder = {
  "attrType", "or", "op", "min", "max"
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

	@XmlElement(name = "op")
	public String getOp()
	{
		return op;
	}

	private String op;

	public void setOp(String op)
	{
		this.op = op;
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

	public boolean isNull()
	{
		return (attrType == null) ||
		  (((min == null) || min.isNull()) && ((max == null) || max.isNull()));
	}

	public String  toString()
	{
		if(isNull())
			return "Фильтр товара пуст";

		StringBuilder  s = new StringBuilder(92);
		AttrType      at = bean(GetUnity.class).getAttrType(attrType);
		String        op = "substring".equals(this.op)?("подстрока ["):("равен [");

		s.append("Фильтр товара (").append((or)?("ИЛИ)"):("И)"));

		if(at != null) s.append(": '").
		  append((at.getNameLo() != null)?(at.getNameLo()):(at.getName())).
		  append("' ");

		Object m = (min == null)?(null):(min.value());
		Object M = (max == null)?(null):(max.value());
		EX.assertn(m);

		s.append((M != null)?(": от ["):(op)).append(m);
		if(M != null) s.append("] до [").append(M);

		return s.append(']').toString();
	}


	/* Object Interface */

	public boolean equals(Object o)
	{
		return (this == o) || !(o == null || getClass() != o.getClass()) &&
		  CMP.eq(attrType, ((GoodsFilter)o).attrType) && (or == ((GoodsFilter)o).or) &&
		  CMP.eq(min, ((GoodsFilter)o).min) && CMP.eq(max, ((GoodsFilter)o).max);
	}

	public int     hashCode()
	{
		int result = (attrType != null)?(attrType.hashCode()):0;

		result = 31*result + (or?1:0);
		result = 31*result + ((min != null)?(min.hashCode()):0);
		result = 31*result + ((max != null)?(max.hashCode()):0);

		return result;
	}
}