package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.JustObject;


/**
 * Attribute type of a Good combined with
 * value of attribute for concrete Good.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "good-attr")
@XmlType(name = "good-attr", propOrder = {
  "name", "nameLo", "system", "object", "value"
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
	 * Encoded attribute value. May be a string, string-coded
	 * plain type, or JSON object for complex values.
	 */
	public String getValue()
	{
		return value;
	}

	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}
}