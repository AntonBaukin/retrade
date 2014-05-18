package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.XKeyPair;


/**
 * A list with goods prices.
 *
 * Each good of the list must have the key and
 * the price attributes set.
 */
@XmlType(name = "price-list", propOrder = {
  "parent", "XParent"
})
public class PriceList extends CatItem
{
	public static final long serialVersionUID = 0L;


	/**
	 * Parent Item of this Item: a Good, or a Folder.
	 */
	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "parent")
	public Long getParent()
	{
		return (parent == 0L)?(null):(parent);
	}

	public void setParent(Long parent)
	{
		this.parent = (parent == null)?(0L):(parent);
	}

	@XmlElement(name = "xparent")
	public String getXParent()
	{
		return xparent;
	}

	public void setXParent(String xparent)
	{
		this.xparent = xparent;
	}


	/* attributes */

	private long   parent;
	private String xparent;
}