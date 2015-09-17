package com.tverts.api.retrade.document;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.goods.Store;


/**
 * Defines Move Invoice.
 */
@XmlType(name = "move", propOrder = {
  "source", "XSource"
})
public class Move extends BuySell
{
	@XKeyPair(type = Store.class)
	@XmlElement(name = "source-store")
	public Long getSource()
	{
		return source;
	}

	private Long source;

	public void setSource(Long source)
	{
		this.source = source;
	}

	@XmlElement(name = "xsource-store")
	public String getXSource()
	{
		return xsource;
	}

	private String xsource;

	public void setXSource(String xsource)
	{
		this.xsource = xsource;
	}
}