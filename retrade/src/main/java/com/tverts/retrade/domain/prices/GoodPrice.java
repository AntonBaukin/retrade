package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;


/**
 * Element of {@link PriceList}. Defines the
 * cost of the {@link GoodUnit} referred.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      GoodPrice
       extends    NumericBase
       implements TxEntity
{
	/* Good Price */

	public PriceList  getPriceList()
	{
		return priceList;
	}

	private PriceList priceList;

	public void setPriceList(PriceList priceList)
	{
		this.priceList = priceList;
	}

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	private GoodUnit goodUnit;

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	private BigDecimal price;

	public void setPrice(BigDecimal p)
	{
		if((p != null) && (p.scale() != 2))
			p = p.setScale(2);

		this.price = p;
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}
}