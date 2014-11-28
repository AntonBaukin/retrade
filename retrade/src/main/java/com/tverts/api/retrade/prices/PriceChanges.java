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

import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.document.Document;


/**
 * The document listing the change of prices
 * of goods in the Price List referred.
 */
@XmlRootElement(name = "price-changes")
@XmlType(name = "price-changes", propOrder = {
  "list", "XList", "oldPrices", "newPrices", "firmPrices"
})
public class PriceChanges extends Document
{
	/* Price Changes */

	/**
	 * The primary key of the price list that was
	 * updated by the document. If this attribute
	 * is undefined, the document marks the change
	 * of the price lists associations with the
	 * contractors enumerated.
	 */
	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "price-list")
	public Long getList()
	{
		return (list == 0L)?(null):(list);
	}

	private long list;

	public void setList(Long list)
	{
		this.list = (list == null)?(0L):(list);
	}

	@XmlElement(name = "xprice-list")
	public String getXList()
	{
		return xlist;
	}

	private String xlist;

	public void setXList(String xlist)
	{
		this.xlist = xlist;
	}

	/**
	 * Collection of prices of the goods
	 * before the change is done. Only
	 * the goods affected are listed.
	 *
	 * Note that goods removed from the list
	 * are marked with removed attribute and
	 * are not in the list of the new prices.
	 */
	@XmlElement(name = "good-price")
	@XmlElementWrapper(name = "old-prices")
	public List<GoodPrice> getOldPrices()
	{
		return (oldPrices != null)?(oldPrices):
		  (oldPrices = new ArrayList<>(32));
	}

	private List<GoodPrice> oldPrices;

	public void setOldPrices(List<GoodPrice> oldPrices)
	{
		this.oldPrices = oldPrices;
	}

	/**
	 * Collection of prices of the goods
	 * after the change is done.
	 */
	@XmlElement(name = "good-price")
	@XmlElementWrapper(name = "new-prices")
	public List<GoodPrice> getNewPrices()
	{
		return (newPrices != null)?(newPrices):
		  (newPrices = new ArrayList<>(32));
	}

	private List<GoodPrice> newPrices;

	public void setNewPrices(List<GoodPrice> newPrices)
	{
		this.newPrices = newPrices;
	}

	/**
	 * If defined, this sorted list tells the old and the new
	 * updated prices associated with the Contractors affected
	 * by this Price Change Document.
	 */
	@XmlElement(name = "price")
	@XmlElementWrapper(name = "firm-good-price")
	public List<FirmGoodPrice> getFirmPrices()
	{
		return firmPrices;
	}

	private List<FirmGoodPrice> firmPrices;

	public void setFirmPrices(List<FirmGoodPrice> firmPrices)
	{
		this.firmPrices = firmPrices;
	}
}