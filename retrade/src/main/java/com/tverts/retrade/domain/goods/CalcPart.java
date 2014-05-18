package com.tverts.retrade.domain.goods;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericBase;


/**
 * Calculation Part is a reference to some
 * Good Unit with the production volume.
 *
 * The volume means that creating 1 unit of
 * the good owning the calculation requires
 * the volume of the good referred.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class CalcPart extends NumericBase
{
	/* public: bean interface */

	public GoodCalc getGoodCalc()
	{
		return goodCalc;
	}

	public void setGoodCalc(GoodCalc goodCalc)
	{
		this.goodCalc = goodCalc;
	}

	public GoodUnit getGoodUnit()
	{
		return goodUnit;
	}

	public void setGoodUnit(GoodUnit goodUnit)
	{
		this.goodUnit = goodUnit;
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

	/**
	 * When this flag is set, the recursive calculation
	 * of the goods stops on this entry: if the Good Unit
	 * referred is itself a product (calculated), it
	 * would not be split into parts.
	 *
	 * When this flag is not set, the calculation flag
	 * {@link GoodCalc#isSemiReady()} is considered.
	 */
	public Boolean getSemiReady()
	{
		return semiReady;
	}

	public void setSemiReady(Boolean semiReady)
	{
		this.semiReady = semiReady;
	}


	/* part attributes */

	private GoodCalc   goodCalc;
	private GoodUnit   goodUnit;
	private BigDecimal volume;
	private Boolean    semiReady;
}