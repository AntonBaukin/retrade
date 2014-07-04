package com.tverts.api.core;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Just a {@link TwoKeysObject}.
 */
@XmlType(name = "just-object",
  propOrder = { "pkey", "xkey" }
)
public abstract class JustObject
{
	/**
	 * The primary key of the object
	 * in the source database.
	 */
	@XmlAttribute(name = "pkey")
	public Long getPkey()
	{
		return (pkey == 0L)?(null):(pkey);
	}

	private long pkey;

	public void setPkey(Long pkey)
	{
		this.pkey = (pkey == null)?(0L):(pkey);
	}

	/**
	 * The primary key of the object in the
	 * integrated (alternative) database.
	 *
	 * Note that it is never stored
	 * in the primary database!
	 */
	@XmlElement(name = "xkey")
	public String getXkey()
	{
		return xkey;
	}

	private String xkey;

	public void setXkey(String xkey)
	{
		this.xkey = xkey;
	}
}