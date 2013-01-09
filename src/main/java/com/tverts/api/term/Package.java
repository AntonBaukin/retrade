package com.tverts.api.term;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A payload object of terminal interaction
 * with the primary database backend.
 */
@XmlRootElement(name = "package")
@XmlType(propOrder = {
  "version", "tx", "operation", "object", "list"
})
public class Package
{
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
	 * Transaction number. For data requests
	 * means Tx number of the data already
	 * available, and the response must contain
	 * only data of the later updates.
	 *
	 * When receiving the data Tx number is the
	 * oldest transaction updated that data.
	 *
	 * Set this attribute to transfer only
	 * the data deltas.
	 */
	@XmlAttribute(name = "tx")
	public Long getTx()
	{
		return tx;
	}

	public void setTx(Long tx)
	{
		this.tx = tx;
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

	private String version;
	private Long   tx;
	private Object operation;
	private Object object;
	private List   list = new ArrayList(0);
}