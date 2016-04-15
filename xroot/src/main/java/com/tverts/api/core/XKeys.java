package com.tverts.api.core;

/* standard Java classes */

import java.io.Serializable;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Stores the x-keys pair.
 */
@XmlType(name = "xkeys")
public class XKeys implements Serializable, TwoKeysObject
{
	public static final long serialVersionUID = 0L;


	/* public: TwoKeysObject interface */

	@XmlAttribute(name = "pkey")
	public Long    getPkey()
	{
		return pkey;
	}

	public void    setPkey(Long pkey)
	{
		this.pkey = pkey;
	}

	@XmlElement(name = "xkey")
	public String  getXkey()
	{
		return xkey;
	}

	public void    setXkey(String xkey)
	{
		this.xkey = xkey;
	}


	/* the keys */

	private Long   pkey;
	private String xkey;
}