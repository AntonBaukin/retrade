package com.tverts.api.retrade.sells;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.Ignore;
import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.document.Document;
import com.tverts.api.retrade.goods.GoodSell;


/**
 * Receipt from Sells Desk (POS terminal) Session.
 */
@XmlType(name = "receipt", propOrder = {
  "session", "XSession", "deskIndex", "income", "goods"
})
public class SellReceipt extends Document
{
	public static final long serialVersionUID = 0L;


	@XKeyPair(type = SellsSession.class)
	@XmlElement(name = "sells-session")
	public Long getSession()
	{
		return (session == 0L)?(null):(session);
	}

	public void setSession(Long session)
	{
		this.session = (session == null)?(0L):(session);
	}

	@XmlElement(name = "xsells-session")
	public String getXSession()
	{
		return xsession;
	}

	public void setXSession(String xsession)
	{
		this.xsession = xsession;
	}

	/**
	 * Index within the Sells Desk (POS terminal).
	 * Has no system-aware meaning.
	 */
	@XmlElement(name = "desk-index")
	public String getDeskIndex()
	{
		return deskIndex;
	}

	public void setDeskIndex(String deskIndex)
	{
		this.deskIndex = deskIndex;
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

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<GoodSell> getGoods()
	{
		if(goods == null)
			goods = new ArrayList<GoodSell>(8);
		return goods;
	}

	public void setGoods(List<GoodSell> goods)
	{
		this.goods = goods;
	}


	/* attributes */

	private long           session;
	private String         xsession;
	private String         deskIndex;
	private BigDecimal     income;
	private List<GoodSell> goods;
}