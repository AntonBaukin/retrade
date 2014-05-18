package com.tverts.api.retrade.sells;


/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.CatItem;
import com.tverts.api.core.Ignore;


/**
 * Defines a cash and bank sells terminal.
 */
@XmlType(name = "desk", propOrder = {
  "payDesk", "remarks"
})
public class SellsDesk extends CatItem
{
	public static final long serialVersionUID = 0L;


	/**
	 * Primary key of the Payment Terminal
	 * for cash and bank operations.
	 */
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

	@XmlElement(name = "remarks")
	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	/* attributes */

	private Long   payDesk;
	private String remarks;
}