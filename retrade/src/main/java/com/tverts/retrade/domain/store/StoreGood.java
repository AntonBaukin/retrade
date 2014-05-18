package com.tverts.retrade.domain.store;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (goods + prices + invoices) */

import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.invoice.InvoiceStateFixed;


/**
 * Good Unit volume added or removed from a Trade Store.
 * The volume is not optional, but the price and cost are.
 *
 * @author anton.baukin@gmail.com
 */
public class StoreGood extends NumericBase
{
	/* public: StoreGood (bean) interface */

	public TradeStore getStore()
	{
		return store;
	}

	public void       setStore(TradeStore store)
	{
		this.store = store;
	}

	public InvoiceStateFixed
	                  getInvoiceState()
	{
		return invoiceState;
	}

	public void       setInvoiceState(InvoiceStateFixed invoiceState)
	{
		this.invoiceState = invoiceState;
	}

	public GoodUnit   getGoodUnit()
	{
		return goodUnit;
	}

	public void       setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public BigDecimal getVolumePositive()
	{
		return volumePositive;
	}

	public void       setVolumePositive(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.volumePositive = v;
	}

	public BigDecimal getVolumeNegative()
	{
		return volumeNegative;
	}

	public void       setVolumeNegative(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.volumeNegative = v;
	}

	/**
	 * Indicates the volume left in the Store.
	 * Used in Volume Check Invoices.
	 */
	public BigDecimal getVolumeLeft()
	{
		return volumeLeft;
	}

	public void       setVolumeLeft(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.volumeLeft = v;
	}


	/* persisted attributes & references */

	private TradeStore        store;
	private InvoiceStateFixed invoiceState;
	private GoodUnit          goodUnit;
	private BigDecimal        volumePositive;
	private BigDecimal        volumeNegative;
	private BigDecimal        volumeLeft;
}