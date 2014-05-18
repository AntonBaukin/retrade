package com.tverts.api.retrade.goods;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.XKeyPair;


/**
 * Item of goods tree. May be a goods folder,
 * or a Good Unit reference. In the latter case
 * the primary key (and x-key) of an Item is
 * a Good Unit primary key.
 *
 * Catalogue code and name are defined for
 * tree folders only, but not for goods.
 *
 *
 * @author anton.baukin@gmail.com.
 */
@XmlType(name = "tree-item", propOrder = {
  "good", "folder", "parent", "XParent"
})
public class TreeItem extends CatItem
{
	public static final long serialVersionUID = 0L;


	@XmlElement(name = "good")
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	/**
	 * Parent Item of this Item: a Good, or a Folder.
	 */
	@XKeyPair(type = TreeItem.class)
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

	@XmlAttribute(name = "folder")
	public Boolean isFolder()
	{
		return folder?(Boolean.TRUE):(null);
	}

	public void setFolder(Boolean folder)
	{
		this.folder = Boolean.TRUE.equals(folder);
	}


	/* attributes */

	private long    good;
	private long    parent;
	private String  xparent;
	private boolean folder;
}