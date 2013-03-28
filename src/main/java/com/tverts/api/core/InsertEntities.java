package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


/**
 * Send this query to update the objects given
 * on the server side. The objects may be of
 * different Unity Types.
 */
@XmlType(name = "insert-entities")
public class InsertEntities implements Serializable
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "holder")
	@XmlElementWrapper(name = "entities", required = true)
	public List<Holder> getEntities()
	{
		return entities;
	}

	public void setEntities(List<Holder> entities)
	{
		this.entities = entities;
	}


	/* the entities */

	private List<Holder> entities =
	  new ArrayList<Holder>(4);
}