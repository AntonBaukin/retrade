package com.tverts.api;

/* standard Java classes */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A payload object to access entities
 * in the primary database backend.
 *
 * The instances of this class are the
 * most frequent objects of Ping-Pong.
 */
@XmlRootElement(name = "package")
@XmlType(propOrder = {
  "version", "operation", "object", "list"
})
public class Payload implements Serializable
{
	public static final long serialVersionUID = 0L;

	@XmlAttribute(name = "version")
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * Object with the request-response operation.
	 */
	@XmlElement(name = "operation", required = true)
	public Object getOperation()
	{
		return operation;
	}

	public void setOperation(Object operation)
	{
		this.operation = operation;
	}

	/**
	 * Payload (data) object of the package.
	 *
	 */
	@XmlElement(name = "object")
	public Object getObject()
	{
		return object;
	}

	public void setObject(Object object)
	{
		this.object = object;
	}

	/**
	 * A list of data objects.
	 */
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "object")
	public List getList()
	{
		return list;
	}

	public void setList(List list)
	{
		this.list = list;
	}


	/* payload object */

	private String  version;
	private Object  operation;
	private Object  object;
	private List    list = new ArrayList(0);
}