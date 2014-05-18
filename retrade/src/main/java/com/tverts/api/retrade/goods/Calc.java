package com.tverts.api.retrade.goods;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.document.Document;


/**
 * Good calculation (formula).
 */
@XmlType(name = "good-calc", propOrder = {
  "good", "XGood", "amount", "items"
})
public class Calc extends Document
{
	public static final long serialVersionUID = 0L;


	@XKeyPair(type = Good.class)
	@XmlElement(name = "good")
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	@XmlElement(name = "amount")
	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	@XmlElement(name = "part")
	@XmlElementWrapper(name = "parts")
	public List<CalcItem> getItems()
	{
		return items;
	}

	public void setItems(List<CalcItem> items)
	{
		this.items = items;
	}


	/* attributes */

	private long           good;
	private String         xgood;
	private BigDecimal     amount;
	private List<CalcItem> items = new ArrayList<CalcItem>(0);
}