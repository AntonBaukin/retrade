package com.tverts.api.retrade.prices;

/* Java */

import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;


/**
 * A list with goods prices.
 *
 * Each good of the list must have the key and
 * the price attributes set.
 */
@XmlRootElement(name = "price-list")
@XmlType(name = "price-list", propOrder = { "items" })
public class PriceList extends CatItem
{
	@XmlElement(name = "item")
	@XmlElementWrapper(name = "items")
	public List<PriceItem> getItems()
	{
		return (items != null)?(items):
		  (items = new ArrayList<PriceItem>(16));
	}

	private List<PriceItem> items;

	public void setItems(List<PriceItem> items)
	{
		this.items = items;
	}
}