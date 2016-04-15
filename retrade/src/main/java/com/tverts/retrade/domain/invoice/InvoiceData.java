package com.tverts.retrade.domain.invoice;

/* standard Java classes */

import java.util.ArrayList;
import java.util.List;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (trade stores) */

import com.tverts.retrade.domain.store.TradeStore;


/**
 * Data related to an Invoice.
 *
 * In the most cases, the data type depends
 * on the concrete (Unity) Type of the Invoice,
 * and the data class is a constant during
 * the Invoice instance life. (What is an
 * opposite to an Invoice state.)
 *
 * Invoice is always related to some Trade Store.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class InvoiceData extends NumericBase
{
	/* public: InvoiceData (bean) interface */

	public Invoice       getInvoice()
	{
		return invoice;
	}

	public void          setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}

	public TradeStore    getStore()
	{
		return store;
	}

	public void          setStore(TradeStore store)
	{
		this.store = store;
	}

	/**
	 * Tells that the resulting goods of the Invoice
	 * were altered. This means that the Store Goods
	 * differ from the goods of the list. The meaning
	 * of difference depends on the Invoice type.
	 */
	public boolean       isAltered()
	{
		return (getSubType() != null);
	}

	/**
	 * Character denoting the type of an altered Invoice.
	 * Regular Invoices have this field undefined.
	 */
	public Character     getSubType()
	{
		return subType;
	}

	public void          setSubType(Character subType)
	{
		this.subType = subType;
	}

	/**
	 * Resulting goods of an altered Invoice.
	 */
	public List<ResGood> getResGoods()
	{
		return (resGoods != null)?(resGoods):
		  (resGoods = new ArrayList<ResGood>(0));
	}

	public void          setResGoods(List<ResGood> resGoods)
	{
		this.resGoods = resGoods;
	}


	/* public: InvoiceData (goods) interface */

	public abstract List<? extends InvGood> getGoods();


	/* links to the invoice and the store */

	private Invoice       invoice;
	private TradeStore    store;
	private Character     subType;
	private List<ResGood> resGoods;
}