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

import com.tverts.api.retrade.document.Document;


/**
 * The document listing the change of prices
 * of goods in the Price List referred.
 */
@XmlRootElement(name = "price-changes")
@XmlType(name = "price-changes",
  propOrder = { "oldPrices", "newPrices" }
)
public class PriceChanges extends Document
{
	/**
	 * Collection of prices of the goods
	 * before the change is done. Only
	 * the goods affected are listed.
	 *
	 * Note that goods removed from the list
	 * are marked with removed attribute and
	 * are not in the list of the ew prices.
	 */
	@XmlElement(name = "price-item")
	@XmlElementWrapper(name = "old-prices")
	public List<PriceItem> getOldPrices()
	{
		return (oldPrices != null)?(oldPrices):
		  (oldPrices = new ArrayList<PriceItem>(32));
	}

	private List<PriceItem> oldPrices;

	public void setOldPrices(List<PriceItem> oldPrices)
	{
		this.oldPrices = oldPrices;
	}

	/**
	 * Collection of prices of the goods
	 * after the change is done.
	 */
	@XmlElement(name = "price-item")
	@XmlElementWrapper(name = "new-prices")
	public List<PriceItem> getNewPrices()
	{
		return (newPrices != null)?(newPrices):
		  (newPrices = new ArrayList<PriceItem>(32));
	}

	private List<PriceItem> newPrices;

	public void setNewPrices(List<PriceItem> newPrices)
	{
		this.newPrices = newPrices;
	}
}