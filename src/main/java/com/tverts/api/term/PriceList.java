package com.tverts.api.term;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


/**
 * A list with goods prices.
 *
 * Each good of the list must have the key and
 * the price attributes set, and only them.
 */
@XmlType(name = "price-list", propOrder = {
  "key", "removed", "code", "title", "goods"
})
public class PriceList
{
	/**
	 * Primary key of the Price List assigned
	 * by the source database.
	 */
	@XmlAttribute(name = "key", required = true)
	public long getKey()
	{
		return key;
	}

	public void setKey(long key)
	{
		this.key = key;
	}

	@XmlAttribute(name = "removed")
	public Boolean isRemoved()
	{
		return Boolean.TRUE.equals(removed)?(Boolean.TRUE):(null);
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = removed;
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

	@XmlElement(name = "title")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlElementWrapper(name = "goods")
	@XmlElement(name = "good")
	public List<Good> getGoods()
	{
		return goods;
	}

	public void setGoods(List<Good> goods)
	{
		this.goods = goods;
	}


	/* Object interface */

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(o == null || getClass() != o.getClass())
			return false;

		PriceList priceList = (PriceList)o;
		return key == priceList.key;
	}

	@Override
	public int hashCode()
	{
		return (int)(key ^ (key >>> 32));
	}

	/* attributes */

	private long    key;
	private Boolean removed;
	private String  code;
	private String  title;


	/* the goods of the list */

	private List<Good> goods =
	  new ArrayList<Good>(32);
}