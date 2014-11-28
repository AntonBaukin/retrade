package com.tverts.api.retrade.prices;

/* Java */

import java.math.BigDecimal;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;

/* com.tverts: retrade api */

import com.tverts.api.retrade.firm.Contractor;
import com.tverts.api.retrade.goods.Good;


/**
 * Old and new prices of a Good Unit available
 * to sell for the given Contractor.
 */
@XmlRootElement(name = "firm-good-price")
@XmlType(name = "firm-good-price", propOrder = {
  "contractor", "XContractor", "good", "XGood",
  "oldList", "XOldList", "oldPrice",
  "newList", "XNewList", "newPrice"
})
public class FirmGoodPrice implements Comparable<FirmGoodPrice>
{
	/* Firm Good Price */

	@XKeyPair(type = Contractor.class)
	public Long getContractor()
	{
		return (contractor == 0L)?(null):(contractor);
	}

	private long contractor;

	public void setContractor(Long contractor)
	{
		this.contractor = (contractor == null)?(0L):(contractor);
	}

	@XmlElement(name = "xcontractor")
	public String getXContractor()
	{
		return xcontractor;
	}

	private String xcontractor;

	public void setXContractor(String xcontractor)
	{
		this.xcontractor = xcontractor;
	}

	@XKeyPair(type = Good.class)
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	private long good;

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	private String xgood;

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "old-price-list")
	public Long getOldList()
	{
		return (oldList == 0L)?(null):(oldList);
	}

	private long oldList;

	public void setOldList(Long oldList)
	{
		this.oldList = (oldList == null)?(0L):(oldList);
	}

	@XmlElement(name = "xold-price-list")
	public String getXOldList()
	{
		return xoldList;
	}

	private String xoldList;

	public void setXOldList(String xoldList)
	{
		this.xoldList = xoldList;
	}

	@XmlElement(name = "old-price")
	public BigDecimal getOldPrice()
	{
		return oldPrice;
	}

	private BigDecimal oldPrice;

	public void setOldPrice(BigDecimal oldPrice)
	{
		this.oldPrice = oldPrice;
	}

	@XKeyPair(type = PriceList.class)
	@XmlElement(name = "new-price-list")
	public Long getNewList()
	{
		return (newList == 0L)?(null):(newList);
	}

	private long newList;

	public void setNewList(Long newList)
	{
		this.newList = (newList == null)?(0L):(newList);
	}

	@XmlElement(name = "xnew-price-list")
	public String getXNewList()
	{
		return xnewList;
	}

	private String xnewList;

	public void setXNewList(String xnewList)
	{
		this.xnewList = xnewList;
	}

	@XmlElement(name = "new-price")
	public BigDecimal getNewPrice()
	{
		return newPrice;
	}

	private BigDecimal newPrice;

	public void setNewPrice(BigDecimal newPrice)
	{
		this.newPrice = newPrice;
	}


	/* Comparable */

	/**
	 * The items are ordered by (Contractor, Good) pair.
	 */
	public int compareTo(FirmGoodPrice o)
	{
		int x = Long.compare(contractor, o.contractor);
		return (x != 0)?(x):Long.compare(good, o.good);
	}
}