package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: support */

import com.tverts.api.support.CMP;
import com.tverts.api.support.EX;


/**
 * Class that simply stores JSON-encoding string.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "json-string")
@XmlType(name = "json-string", propOrder = { "type", "json" })
public class JString
{
	public JString()
	{}

	public JString(String json)
	{
		this.json = EX.asserts(json);
	}

	public String getType()
	{
		return type;
	}

	private String type;

	public void setType(String type)
	{
		this.type = type;
	}

	@XmlElement(required = true)
	public String getJson()
	{
		return json;
	}

	private String json;

	public void setJson(String json)
	{
		this.json = json;
	}


	/* Object Interface */

	public boolean equals(Object o)
	{
		return (this == o) || !(o == null || getClass() != o.getClass()) &&
		  CMP.eq(type, ((JString)o).type) && CMP.eq(json, ((JString)o).json);
	}

	public int     hashCode()
	{
		return 31 * ((type != null)?(type.hashCode()):0) +
		  ((json != null)?(json.hashCode()):0);
	}
}