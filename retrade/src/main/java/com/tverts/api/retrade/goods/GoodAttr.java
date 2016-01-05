package com.tverts.api.retrade.goods;

/* Java */

import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.JustObject;
import com.tverts.api.core.Value;


/**
 * Attribute type of a Good combined with
 * value of attribute for concrete Good.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "good-attr")
@XmlType(name = "good-attr", propOrder = {
  "name", "nameLo", "system", "array",
  "shared", "object", "value", "values"
})
public class GoodAttr extends JustObject
{
	/**
	 * Name of the attribute up to 255 symbols.
	 */
	public String getName()
	{
		return name;
	}

	private String name;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNameLo()
	{
		return nameLo;
	}

	private String nameLo;

	public void setNameLo(String nameLo)
	{
		this.nameLo = nameLo;
	}

	/**
	 * Tells whether this type may be altered or removed.
	 */
	public boolean isSystem()
	{
		return system;
	}

	private boolean system;

	public void setSystem(boolean system)
	{
		this.system = system;
	}

	/**
	 * Tells that this attribute type supports array of values.
	 */
	public boolean isArray()
	{
		return array;
	}

	private boolean array;

	public void setArray(boolean array)
	{
		this.array = array;
	}

	/**
	 * Tells that this attribute type may be shared.
	 * (Used for attributes of sub-goods.)
	 */
	public boolean isShared()
	{
		return shared;
	}

	private boolean shared;

	public void setShared(boolean shared)
	{
		this.shared = shared;
	}

	/**
	 * JSON-encoded object of the attribute type data.
	 */
	public String getObject()
	{
		return object;
	}

	private String object;

	public void setObject(String object)
	{
		this.object = object;
	}

	/**
	 * Attribute value object: String, Integer,
	 * BigDecimal, or JString.
	 *
	 * Excludes the values list having single object!
	 */
	public Value getValue()
	{
		if(value != null)
			return value;

		if((values != null) && (values.size() == 1))
			return values.get(0);

		return null;
	}

	private Value value;

	public void setValue(Value value)
	{
		this.value = null;
		this.value = value;
	}

	/**
	 * The list of values. Has less priority
	 * over a single value, or
	 *
	 */
	@XmlElement(name = "value")
	@XmlElementWrapper(name = "values")
	public List<Value> getValues()
	{
		return ((values == null) || (values.size() == 1))?(null):(values);
	}

	private List<Value> values;

	/**
	 * Assigns the values list. Don't rely upon that
	 * the list object set is returned in the future!
	 */
	public void setValues(List<Value> values)
	{
		this.values = values;
	}
}