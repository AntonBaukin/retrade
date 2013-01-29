package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Catalogue item. May be used directly,
 * but mostly as a parent class.
 */
@XmlType(name = "catalogue-item", propOrder = {
  "pkey", "tx", "xkey", "removed", "code", "name"
})
public class      CatItem
       implements Serializable, TwoKeysObject
{
	public static final long serialVersionUID = 0L;


	/**
	 * The primary key of the measure
	 * in the source database.
	 */
	@XmlAttribute(name = "pkey")
	public Long getPkey()
	{
		return (pkey == 0L)?(null):(pkey);
	}

	public void setPkey(Long pkey)
	{
		this.pkey = (pkey == null)?(0L):(pkey);
	}

	/**
	 * The transaction number assigned
	 * by the source database.
	 */
	@XmlAttribute(name = "tx")
	public Long getTx()
	{
		return (tx == 0L)?(null):(tx);
	}

	public void setTx(Long tx)
	{
		this.tx = (tx == null)?(0L):(tx);
	}

	/**
	 * The primary key of the measure
	 * in the integrated database.
	 */
	@XmlElement(name = "xkey")
	public String getXkey()
	{
		return xkey;
	}

	public void setXkey(String xkey)
	{
		this.xkey = xkey;
	}

	@XmlAttribute(name = "removed")
	public Boolean isRemoved()
	{
		return removed?(Boolean.TRUE):(null);
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = Boolean.TRUE.equals(removed);
	}

	@XmlElement(name = "code")
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlElement(name = "name")
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	/* attributes */

	private long    pkey;
	private long    tx;
	private String  xkey;
	private boolean removed;
	private String  code;
	private String  name;
}
