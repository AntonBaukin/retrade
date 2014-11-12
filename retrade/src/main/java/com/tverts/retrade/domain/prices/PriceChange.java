package com.tverts.retrade.domain.prices;

/* Java */

import java.math.BigDecimal;
import java.util.Date;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GoodUnit;


/**
 * Item of the {@link RepriceDoc} document.
 *
 * @author anton.baukin@gmail.com
 */
public class PriceChange extends NumericBase
{
	/* Price Change Item */

	public RepriceDoc getRepriceDoc()
	{
		return repriceDoc;
	}

	private RepriceDoc repriceDoc;

	public void setRepriceDoc(RepriceDoc repriceDoc)
	{
		this.repriceDoc = repriceDoc;
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

	public PriceListEntity getPriceList()
	{
		return priceList;
	}

	private PriceListEntity priceList;

	public void setPriceList(PriceListEntity priceList)
	{
		this.priceList = priceList;
	}

	/**
	 * The change time is assigned only when the price
	 * change actually affected the good price.
	 *
	 * It duplicates the value of the document to
	 * allow to select the history of the goods prices!
	 */
	public Date getChangeTime()
	{
		return changeTime;
	}

	private Date changeTime;

	public void setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	/**
	 * The price of the Good Unit at the moment of change.
	 *
	 * Note that when Price Change is inserted in the
	 * middle of the history, the old price of the next
	 * in time object is not updated! This allows to
	 * recover why sell costs in the Invoices in that
	 * period took their values.
	 */
	public BigDecimal getPriceOld()
	{
		return priceOld;
	}

	private BigDecimal priceOld;

	public void setPriceOld(BigDecimal p)
	{
		if((p != null) && (p.scale() != 2))
			p = p.setScale(2);

		this.priceOld = p;
	}

	public BigDecimal getPriceNew()
	{
		return priceNew;
	}

	private BigDecimal priceNew;

	public void setPriceNew(BigDecimal p)
	{
		if((p != null) && (p.scale() != 2))
			p = p.setScale(2);

		this.priceNew = p;
	}

	public int getDocIndex()
	{
		return docIndex;
	}

	private int docIndex;

	public void setDocIndex(int docIndex)
	{
		this.docIndex = docIndex;
	}


	/* public: Object interface */

	/**
	 * The equality of the instances is compared solely
	 * by {@link #getGoodUnit()} instance. This allows
	 * to store the instances in a hash set of the
	 * price change document.
	 */
	public boolean     equals(Object o)
	{
		return (o == this) ||
		  (o instanceof PriceChange) && (getGoodUnit() != null) &&
		  getGoodUnit().equals(((PriceChange)o).getGoodUnit());
	}

	public int         hashCode()
	{
		return (getGoodUnit() == null)?(0):
		  (getGoodUnit().hashCode());
	}
}