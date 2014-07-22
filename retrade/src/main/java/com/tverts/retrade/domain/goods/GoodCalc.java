package com.tverts.retrade.domain.goods;

/* Java */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* com.tverts: api */

import com.tverts.api.retrade.goods.Calc;

/* com.tverts: endure (core) */

import com.tverts.endure.OxNumericTxBase;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Good Calculation defined a method of building good
 * (with 1 unit of it's measure) from other goods that
 * may be built on their own.
 *
 * @author anton.baukin@gmail.com
 */
public class GoodCalc extends OxNumericTxBase
{
	/* Object Extraction */

	public Calc   getOx()
	{
		Calc c = (Calc) super.getOx();
		if(c == null) setOx(c = new Calc());
		return c;
	}

	public void   setOx(Object ox)
	{
		EX.assertx(ox instanceof Calc);
		super.setOx(ox);
	}

	public void   updateOx()
	{
		super.updateOx();

		Calc c; if((c = this.getOx()) != null)
		{
			//=: related good
			c.setGood((goodUnit == null)?(null):goodUnit.getPrimaryKey());

			//=: super good
			c.setSuperGood((superGood == null)?(null):superGood.getPrimaryKey());

			//=: open time
			openTime = c.getTime();

			//=: close time
			closeTime = c.getCloseTime();

			//=: semi-ready
			semiReady = c.isSemiReady();
		}
	}


	/* Good Calculation */

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	private GoodUnit goodUnit;

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
	}

	public Date getOpenTime()
	{
		return openTime;
	}

	private Date openTime;

	public void setOpenTime(Date openTime)
	{
		this.openTime = openTime;
	}

	public Date getCloseTime()
	{
		return closeTime;
	}

	private Date closeTime;

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

	private boolean semiReady;

	public void setSemiReady(boolean semiReady)
	{
		this.semiReady = semiReady;
	}

	public GoodUnit getSuperGood()
	{
		return superGood;
	}

	private GoodUnit superGood;

	public void setSuperGood(GoodUnit superGood)
	{
		this.superGood = superGood;
	}

	public List<CalcPart> getParts()
	{
		return (parts != null)?(parts):
		  (parts = new ArrayList<CalcPart>(4));
	}

	private List<CalcPart> parts;

	public void setParts(List<CalcPart> parts)
	{
		this.parts = parts;
	}
}