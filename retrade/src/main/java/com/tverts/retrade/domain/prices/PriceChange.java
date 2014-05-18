package com.tverts.retrade.domain.prices;

/* standard Java classes */

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
	/* public: PriceChange (bean) access */

	public RepriceDoc  getRepriceDoc()
	{
		return repriceDoc;
	}

	public void        setRepriceDoc(RepriceDoc repriceDoc)
	{
		this.repriceDoc = repriceDoc;
	}

	public GoodUnit    getGoodUnit()
	{
		return goodUnit;
	}

	public void        setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	/**
	 * The change time is assigned only when the price
	 * change actually affected the good price.
	 */
	public Date        getChangeTime()
	{
		return changeTime;
	}

	public void        setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	public BigDecimal  getPriceOld()
	{
		return priceOld;
	}

	public void        setPriceOld(BigDecimal p)
	{
		if((p != null) && (p.scale() != 2))
			p = p.setScale(2);

		this.priceOld = p;
	}

	public BigDecimal  getPriceNew()
	{
		return priceNew;
	}

	public void        setPriceNew(BigDecimal p)
	{
		if((p != null) && (p.scale() != 2))
			p = p.setScale(2);

		this.priceNew = p;
	}

	public int         getDocIndex()
	{
		return docIndex;
	}

	public void        setDocIndex(int docIndex)
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


	/* persisted attributes */

	private RepriceDoc repriceDoc;
	private GoodUnit   goodUnit;
	private Date       changeTime;
	private BigDecimal priceOld;
	private BigDecimal priceNew;
	private int        docIndex;
}