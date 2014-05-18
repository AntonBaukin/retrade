package com.tverts.api.retrade.document;

/* standard Java classes */

import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.goods.GoodSell;
import com.tverts.api.retrade.goods.Store;


/**
 * Defines Move Invoice.
 */
@XmlType(name = "move", propOrder = {
  "source", "XSource", "goods"
})
public class Move extends BuySell
{
	public static final long serialVersionUID = 0L;


	@XKeyPair(type = Store.class)
	@XmlElement(name = "source-store")
	public Long getSource()
	{
		return source;
	}

	public void setSource(Long source)
	{
		this.source = source;
	}

	@XmlElement(name = "xsource-store")
	public String getXSource()
	{
		return xsource;
	}

	public void setXSource(String xsource)
	{
		this.xsource = xsource;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<GoodSell> getGoods()
	{
		return super.getGoods();
	}


	/* the source store */

	private Long    source;
	private String  xsource;
}