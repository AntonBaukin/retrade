package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines an entity in it's transfer version.
 */
@XmlType(name = "holder", propOrder = {
  "typeClass", "typeName", "entity"
})
public class Holder implements Serializable
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "entity", required = true)
	public Object getEntity()
	{
		return entity;
	}

	public void setEntity(Object entity)
	{
		this.entity = entity;
	}

	@XmlElement(name = "type-class")
	public Class getTypeClass()
	{
		return typeClass;
	}

	public void setTypeClass(Class typeClass)
	{
		this.typeClass = typeClass;
	}

	@XmlElement(name = "type-name")
	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}


	/* entity pack */

	private Object entity;
	private Class  typeClass;
	private String typeName;
}