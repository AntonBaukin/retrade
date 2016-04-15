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
@XmlType(name = "update-entities")
public class UpdateEntities implements Serializable
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


	/* public: Object interface */

	public String toString()
	{
		StringBuilder s = new StringBuilder(128);
		s.append(getClass().getSimpleName());

		//~: take the first holder
		Holder h = ((entities == null) || entities.isEmpty())?(null):(entities.get(0));
		if(h == null) return s.toString();

		s.append(" [").append(entities.size()).append(']');

		if(h.getTypeClass() != null)
			s.append(" -> ").append(h.getTypeClass().getSimpleName());

		if(h.getTypeName() != null)
			s.append(" {").append(h.getTypeName()).append('}');

		return s.toString();
	}


	/* the entities */

	private List<Holder> entities =
	  new ArrayList<Holder>(4);
}