package com.tverts.api.retrade.document;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: api */

import com.tverts.api.core.XKeyPair;
import com.tverts.api.retrade.firm.Firm;
import com.tverts.api.retrade.goods.GoodSell;
import com.tverts.api.retrade.goods.Store;


/**
 * Abstraction of Buy-Sell invoices
 * and the alike documents.
 */
@XmlType(name = "buy-sell", propOrder = {
  "store", "XStore", "contractor", "XContractor", "goods"
})
public abstract class BuySell extends Document
{
	public static final long serialVersionUID = 0L;


	/**
	 * The store of the buy-sell operation.
	 * For sell operation defines the store
	 * the goods were taken from; for buy
	 * one defines the store the goods were
	 * placed in.
	 */
	@XKeyPair(type = Store.class)
	@XmlElement(name = "store")
	public Long getStore()
	{
		return (store == 0L)?(null):(store);
	}

	public void setStore(Long store)
	{
		this.store = (store == null)?(0L):(store);
	}

	@XmlElement(name = "xstore")
	public String getXStore()
	{
		return xstore;
	}

	public void setXStore(String xstore)
	{
		this.xstore = xstore;
	}

	@XKeyPair(type = Firm.class)
	@XmlElement(name = "contractor")
	public Long getContractor()
	{
		return contractor;
	}

	public void setContractor(Long contractor)
	{
		this.contractor = contractor;
	}

	@XmlElement(name = "xcontractor")
	public String getXContractor()
	{
		return xcontractor;
	}

	public void setXContractor(String xcontractor)
	{
		this.xcontractor = xcontractor;
	}

	@XmlElement(name = "good")
	@XmlElementWrapper(name = "goods")
	public List<GoodSell> getGoods()
	{
		return goods;
	}

	public void setGoods(List<GoodSell> goods)
	{
		this.goods = goods;
	}


	/* the goods of the invoice & the store & the contractor */

	private long           store;
	private String         xstore;
	private Long           contractor;
	private String         xcontractor;

	private List<GoodSell> goods =
	  new ArrayList<GoodSell>(0);
}