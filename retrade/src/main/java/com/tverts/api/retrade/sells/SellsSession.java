package com.tverts.api.retrade.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.Date;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.Ignore;
import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.document.Document;


/**
 * Sells terminal (desk) Session.
 */
@XmlType(name = "session", propOrder = {
  "closeTime", "sellsDesk", "XSellsDesk", "payDesk",
  "income", "itemsUpdated"
})
public class SellsSession extends Document
{
	public static final long serialVersionUID = 0L;


	@Ignore
	@XmlElement(name = "close-time")
	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	@XKeyPair(type = SellsDesk.class)
	@XmlElement(name = "sells-desk")
	public Long getSellsDesk()
	{
		return sellsDesk;
	}

	public void setSellsDesk(Long sellsDesk)
	{
		this.sellsDesk = sellsDesk;
	}

	@XmlElement(name = "xsells-desk")
	public String getXSellsDesk()
	{
		return XSellsDesk;
	}

	public void setXSellsDesk(String XSellsDesk)
	{
		this.XSellsDesk = XSellsDesk;
	}

	@Ignore
	@XmlElement(name = "pay-desk")
	public Long getPayDesk()
	{
		return payDesk;
	}

	public void setPayDesk(Long payDesk)
	{
		this.payDesk = payDesk;
	}

	@Ignore
	@XmlElement(name = "income")
	public BigDecimal getIncome()
	{
		return income;
	}

	public void setIncome(BigDecimal income)
	{
		this.income = income;
	}

	/**
	 * Used when updating Sells Session: tells that
	 * the receipts were changed, and the Session
	 * must be updated with all the derived entities
	 * (such as Sells Invoices and Payments).
	 *
	 * Note that updating Session with items updated
	 * is a time-consuming operation.
	 */
	@Ignore
	public boolean isItemsUpdated()
	{
		return itemsUpdated;
	}

	public void setItemsUpdated(boolean itemsUpdated)
	{
		this.itemsUpdated = itemsUpdated;
	}


	/* attributes */

	private Date       closeTime;
	private Long       sellsDesk;
	private String     XSellsDesk;
	private Long       payDesk;
	private BigDecimal income;
	private boolean    itemsUpdated;
}