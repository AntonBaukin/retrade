package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (goods + prices + stores) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Part of Sell Receipt of a Good sold via Sells Desk.
 *
 * @author anton.baukin@gmail.com
 */
public class GoodSell extends NumericBase
{
	/* public: GoodSell (bean) interface */

	public SellReceipt getReceipt()
	{
		return receipt;
	}

	public void setReceipt(SellReceipt receipt)
	{
		this.receipt = receipt;
	}

	public TradeStore getStore()
	{
		return store;
	}

	public void setStore(TradeStore store)
	{
		this.store = store;
	}

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public GoodPrice getGoodPrice()
	{
		return goodPrice;
	}

	public void setGoodPrice(GoodPrice goodPrice)
	{
		this.goodPrice = goodPrice;
	}

	public BigDecimal getVolume()
	{
		return volume;
	}

	public void setVolume(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.volume = v;
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCost(BigDecimal c)
	{
		if((c != null) && (c.scale() != 10))
			c = c.setScale(10);

		this.cost = c;
	}

	/**
	 * Denotes the method of payment:
	 *  · B  for bank payment;
	 *  · C  for cash payment (the default).
	 */
	public char getPayFlag()
	{
		return payFlag;
	}

	public void setPayFlag(char f)
	{
		EX.assertx((f == 'C') || (f == 'B'));
		this.payFlag = f;
	}


	/* persisted attributes & references */

	private SellReceipt receipt;
	private TradeStore  store;
	private GoodUnit    goodUnit;
	private GoodPrice   goodPrice;
	private BigDecimal  volume;
	private BigDecimal  cost;
	private char        payFlag = 'C';
}