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
@XmlType(name = "desk", propOrder = { "payDesk" })
public class SellsDesk extends CatItem
{
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

	private Long payDesk;

	public void setPayDesk(Long payDesk)
	{
		this.payDesk = payDesk;
	}
}