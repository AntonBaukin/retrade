package com.tverts.api.retrade.goods;

/* Java */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* Java API for XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: api */

import com.tverts.api.core.JustTxObject;
import com.tverts.api.core.Timed;
import com.tverts.api.core.XKeyPair;
import com.tverts.api.support.EX;
import com.tverts.api.support.TimestampAdapter;


/**
 * Good calculation (formula).
 */
@XmlRootElement(name = "good-calc")
@XmlType(name = "good-calc", propOrder = {
  "good", "XGood", "time", "closeTime",
  "semiReady", "superGood", "XSuperGood",
  "subCode" , "subVolume", "amount",
  "remarks", "items"
})
public class      Calc
       extends    JustTxObject
       implements Timed
{
	/* Good Calculation */

	@XKeyPair(type = Good.class)
	@XmlElement(name = "good")
	public Long getGood()
	{
		return (good == 0L)?(null):(good);
	}

	private long good;

	public void setGood(Long good)
	{
		this.good = (good == null)?(0L):(good);
	}

	@XmlElement(name = "xgood")
	public String getXGood()
	{
		return xgood;
	}

	private String xgood;

	public void setXGood(String xgood)
	{
		this.xgood = xgood;
	}

	@XmlElement(name = "open-time")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Date getTime()
	{
		return openTime;
	}

	private Date openTime;

	public void setTime(Date openTime)
	{
		this.openTime = openTime;
	}

	@XmlElement(name = "close-time")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
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
	@XmlElement(name = "semi-ready")
	public boolean isSemiReady()
	{
		return semiReady;
	}

	private boolean semiReady;

	public void setSemiReady(boolean semiReady)
	{
		this.semiReady = semiReady;
	}

	@XKeyPair(type = Good.class)
	@XmlElement(name = "super-good")
	public Long getSuperGood()
	{
		return (superGood == 0L)?(null):(superGood);
	}

	private long superGood;

	public void setSuperGood(Long superGood)
	{
		this.superGood = (superGood == null)?(0L):(superGood);
	}

	@XmlElement(name = "xsuper-good")
	public String getXSuperGood()
	{
		return xsuperGood;
	}

	private String xsuperGood;

	public void setXSuperGood(String xsuperGood)
	{
		this.xsuperGood = xsuperGood;
	}

	/**
	 * Sub-code of the derived good.
	 */
	@XmlElement(name = "sub-code")
	public String getSubCode()
	{
		return subCode;
	}

	private String subCode;

	public void setSubCode(String subCode)
	{
		this.subCode = subCode;
	}

	/**
	 * The volume (amount) of the derived good.
	 */
	@XmlElement(name = "sub-volume")
	public BigDecimal getSubVolume()
	{
		return subVolume;
	}

	private BigDecimal subVolume;

	public void setSubVolume(BigDecimal v)
	{
		EX.assertx((v == null) || (v.scale() <= 8));
		this.subVolume = v;
	}

	/**
	 * Support value to use in the integration databases.
	 */
	@XmlElement(name = "amount")
	public BigDecimal getAmount()
	{
		return amount;
	}

	private BigDecimal amount;

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * Comments on this Calculation.
	 */
	@XmlElement(name = "remarks")
	public String getRemarks()
	{
		return remarks;
	}

	private String remarks;

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	@XmlElement(name = "part")
	@XmlElementWrapper(name = "parts")
	public List<CalcItem> getItems()
	{
		return items;
	}

	private List<CalcItem> items = new ArrayList<CalcItem>(4);

	public void setItems(List<CalcItem> items)
	{
		this.items = items;
	}
}