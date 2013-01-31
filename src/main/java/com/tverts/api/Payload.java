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
  "version", "txMin", "txMax", "first", "more",
  "operation", "object", "list"
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
	 * Transaction borders of the range [Tx-min; Tx-max].
	 * (Tx-max border is optional.)
	 *
	 * When selecting huge amount of data in several
	 * requests to the server, you need to control that
	 * updates from transactions between the requests
	 * not to interfere the results. With Tx borders
	 * you filter out that updates, but some malicious
	 * effects are still possible!
	 *
	 * Set this attributes to request transfer
	 * returning the data deltas.
	 */
	@XmlAttribute(name = "tx-min")
	public Long getTxMin()
	{
		return txMin;
	}

	public void setTxMin(Long txMin)
	{
		this.txMin = txMin;
	}

	@XmlAttribute(name = "tx-max")
	public Long getTxMax()
	{
		return txMax;
	}

	public void setTxMax(Long txMax)
	{
		this.txMax = txMax;
	}

	/**
	 * Set offset in the list request. Note that combining
	 * this attribute with Tx borders may not guarantee
	 * transaction coherence if rows may be deleted.
	 *
	 * But in the system top-level objects may not be deleted,
	 * but only marked as removed.
	 */
	@XmlAttribute(name = "first")
	public Long getFirst()
	{
		return first;
	}

	public void setFirst(Long first)
	{
		this.first = first;
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
	private Long    txMin;
	private Long    txMax;
	private Long    first;
	private Object  operation;
	private Object  object;
	private List    list = new ArrayList(0);
	private boolean more;
}