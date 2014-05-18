package com.tverts.retrade.domain.sells;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Stores properties of a {@link SellReceipt}
 * and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-unit")
public class SellReceiptView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: bean interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public SellPayOp getPayOp()
	{
		return payOp;
	}

	public void setPayOp(SellPayOp payOp)
	{
		this.payOp = payOp;
	}

	@XmlElement
	public BigDecimal getIncome()
	{
		return (getPayOp() == null)?(null):
		  (getPayOp().calcTotal().setScale(2, BigDecimal.ROUND_HALF_EVEN));
	}

	@XmlElement
	public String getPayFlag()
	{
		return (getPayOp() == null)?(null):(getPayOp().payFlag());
	}

	public Integer getIndex()
	{
		return index;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}


	/* public: initialization interface */

	public SellReceiptView init(Object obj)
	{
		if(obj instanceof SellReceipt)
			return this.init((SellReceipt) obj);

		return this;
	}

	public SellReceiptView init(SellReceipt r)
	{
		this.objectKey = r.getPrimaryKey();
		this.code = r.getCode();
		this.time = r.getTime();
		this.payOp = r.payOp();

		return this;
	}

	public SellReceiptView initIndex(Integer i)
	{
		this.index = i;
		return this;
	}


	/* properties of the sells session */

	private Long       objectKey;
	private String     code;
	private Date       time;
	private SellPayOp  payOp;
	private Integer    index;
}