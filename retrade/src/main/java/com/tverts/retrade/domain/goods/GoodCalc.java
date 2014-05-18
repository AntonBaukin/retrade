package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;
import com.tverts.endure.TxEntity;


/**
 * Good Calculation defined a method of building good
 * (with 1 unit of it's measure) from other goods that
 * may be built on their own.
 *
 * @author anton.baukin@gmail.com
 */
public class      GoodCalc
       extends    NumericBase
       implements TxEntity
{
	/* public: bean interface */

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public Date getOpenTime()
	{
		return openTime;
	}

	public void setOpenTime(Date openTime)
	{
		this.openTime = openTime;
	}

	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	/**
	 * If Good is a semi-ready, it may be bought
	 * (placed in Buy Invoices). Note that products
	 * (goods having calculation) may not be.
	 */
	public boolean isSemiReady()
	{
		return semiReady;
	}

	public void setSemiReady(boolean semiReady)
	{
		this.semiReady = semiReady;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public List<CalcPart> getParts()
	{
		return (parts != null)?(parts):
		  (parts = new ArrayList<CalcPart>(1));
	}

	public void setParts(List<CalcPart> parts)
	{
		this.parts = parts;
	}

	public GoodUnit getSuperGood()
	{
		return superGood;
	}

	public void setSuperGood(GoodUnit superGood)
	{
		this.superGood = superGood;
	}

	public String getSubCode()
	{
		return subCode;
	}

	public void setSubCode(String subCode)
	{
		this.subCode = subCode;
	}

	public BigDecimal getSubVolume()
	{
		return subVolume;
	}

	public void setSubVolume(BigDecimal v)
	{
		if((v != null) && (v.scale() != 8))
			v = v.setScale(8);

		this.subVolume = v;
	}


	/* public: TxEntity interface */

	public Long getTxn()
	{
		return (txn == 0L)?(null):(txn);
	}

	private long txn;

	/**
	 * When updated, transaction number is also
	 * copied to the unified mirror.
	 */
	public void setTxn(Long txn)
	{
		this.txn = (txn == null)?(0L):(txn);
	}


	/* calculation attributes and entities */

	private GoodUnit       goodUnit;
	private Date           openTime;
	private Date           closeTime;
	private boolean        semiReady;
	private String         remarks;
	private List<CalcPart> parts;

	private GoodUnit       superGood;
	private String         subCode;
	private BigDecimal     subVolume;
}