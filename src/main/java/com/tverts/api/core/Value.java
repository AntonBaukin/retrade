package com.tverts.api.core;

/* Java */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.support.EX;


/**
 * Value object.
 *
 * @author anton.baukin@gmail.com.
 */
@XmlRootElement(name = "value")
@XmlType(name = "value", propOrder = {
  "text", "integer", "decimal", "json"
})
public class Value
{
	/* Bean */

	public String getText()
	{
		return text;
	}

	private String text;

	public void setText(String text)
	{
		this.text = text;
	}

	public Long getInteger()
	{
		return integer;
	}

	private Long integer;

	public void setInteger(Long integer)
	{
		this.integer = integer;
	}

	public BigDecimal getDecimal()
	{
		return decimal;
	}

	private BigDecimal decimal;

	public void setDecimal(BigDecimal decimal)
	{
		this.decimal = decimal;
	}

	public JString getJson()
	{
		return json;
	}

	private JString json;

	public void setJson(JString json)
	{
		this.json = json;
	}


	/* Value Access */

	public Object value()
	{
		return (text != null)?(text):(integer != null)?(integer):
		  (decimal != null)?(decimal):(json != null)?(json):(null);
	}

	public Value  value(Object v)
	{
		text    = null;
		integer = null;
		decimal = null;
		json    = null;

		if(v instanceof String)
			text = (String)v;
		else if(v instanceof Long)
			integer = (Long)v;
		else if(v instanceof Integer)
			integer = ((Integer)v).longValue();
		else if(v instanceof BigDecimal)
			decimal = (BigDecimal)v;
		else if(v instanceof JString)
			json = (JString)v;
		else if(v != null)
			throw EX.ass("Unsupported value type!");

		return this;
	}
}