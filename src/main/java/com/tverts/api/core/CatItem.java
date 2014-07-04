package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
	@XmlElement(name = "code")
	public String getCode()
	{
		return code;
	}

	private String  code;

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlElement(name = "name")
	public String getName()
	{
		return name;
	}

	private String  name;

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlAttribute(name = "removed")
	public Boolean isRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	private boolean removed;

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}
}