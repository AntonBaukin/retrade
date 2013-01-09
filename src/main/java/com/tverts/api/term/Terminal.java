package com.tverts.api.term;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Computer working as sell terminal.
 */
@XmlType(name = "terminal")
public class Terminal
{
	@XmlAttribute(name = "key", required = true)
	public long getKey()
	{
		return key;
	}

	public void setKey(long key)
	{
		this.key = key;
	}

	/**
	 * Displayed title of the terminal.
	 */
	@XmlElement(name = "title")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}


	/* Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		Terminal terminal = (Terminal)o;
		return (key == terminal.key);
	}

	public int hashCode()
	{
		return (int)(key ^ (key >>> 32));
	}


	/* attributes */

	private long   key;
	private String title;
}