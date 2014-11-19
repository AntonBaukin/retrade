package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Catalogue item. May be used directly,
 * but in the most cases is a parent class.
 */
@XmlType(name = "catalogue-item",
  propOrder = { "removed", "code", "name" }
)
public class      CatItem
       extends    JustTxObject
       implements CodedObject, RemovableObject
{
	public String getCode()
	{
		return code;
	}

	private String  code;

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	private String  name;

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlAttribute
	public Boolean getRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	private boolean removed;

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}
}