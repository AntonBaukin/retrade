package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;

/* com.tverts: support */

import com.tverts.support.CMP;
import com.tverts.support.SU;


/**
 * Sell operation Payment options Java Bean.
 *
 * @author anton.baukin@gmail.com
 */
public class SellPayOp implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: SellPayOp (bean) interface */

	public String      getDeskIndex()
	{
		return deskIndex;
	}

	public void        setDeskIndex(String deskIndex)
	{
		this.deskIndex = deskIndex;
	}

	public BigDecimal  getPayCash()
	{
		return payCash;
	}

	public void        setPayCash(BigDecimal v)
	{
		if(CMP.eqZero(v)) v = null;
		this.payCash = v;
	}

	public BigDecimal  getPayBank()
	{
		return payBank;
	}

	public void        setPayBank(BigDecimal v)
	{
		if(CMP.eqZero(v)) v = null;
		this.payBank = v;
	}


	/* public: support interface */

	public BigDecimal  calcTotal()
	{
		BigDecimal r = BigDecimal.ZERO;

		if(getPayCash() != null)
			r = r.add(getPayCash());

		if(getPayBank() != null)
			r = r.add(getPayBank());

		return r.setScale(5);
	}

	/**
	 * Returns payment flags string for this operation.
	 * The flags are:
	 *  · B  for bank payment;
	 *  · C  for cash payment.
	 *
	 * The flags are separated by '-'.
	 */
	public String      payFlag()
	{
		return SU.sXs(SU.scat("-",

		  (getPayCash() == null)?(null):("C"),
		  (getPayBank() == null)?(null):("B")

		));
	}


	/* payment options */

	private String     deskIndex;
	private BigDecimal payCash;
	private BigDecimal payBank;
}