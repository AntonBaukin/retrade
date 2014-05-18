package com.tverts.retrade.domain.invoice;

/* Java */

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
 * A read-only view on the {@link Invoice} instance.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "invoice")
public class InvoiceView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: InvoiceView (bean) interface */

	public String      getObjectKey()
	{
		return (objectKey == null)?("null"):(objectKey);
	}

	public void        setObjectKey(String objectKey)
	{
		this.objectKey =
		  "null".equals(objectKey)?(null):(objectKey);
	}

	public Long        objectKey()
	{
		return (objectKey == null)?(null):
		  Long.parseLong(objectKey);
	}

	public Long        getDomain()
	{
		return domain;
	}

	public void        setDomain(Long domain)
	{
		this.domain = domain;
	}

	public Long        getInvoiceType()
	{
		return invoiceType;
	}

	public void        setInvoiceType(Long invoiceType)
	{
		this.invoiceType = invoiceType;
	}

	public String      getInvoiceTypeName()
	{
		return invoiceTypeName;
	}

	public void        setInvoiceTypeName(String invoiceTypeName)
	{
		this.invoiceTypeName = invoiceTypeName;
	}

	public Character   getSubType()
	{
		return subType;
	}

	public void        setSubType(Character subType)
	{
		this.subType = subType;
	}

	@XmlElement
	public boolean     isAltered()
	{
		return (subType != null);
	}

	public String      getInvoiceStateName()
	{
		return invoiceStateName;
	}

	public void        setInvoiceStateName(String invoiceStateName)
	{
		this.invoiceStateName = invoiceStateName;
	}

	public String      getInvoiceCode()
	{
		return invoiceCode;
	}

	public void        setInvoiceCode(String invoiceCode)
	{
		this.invoiceCode = invoiceCode;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date        getInvoiceDate()
	{
		return invoiceDate;
	}

	public void        setInvoiceDate(Date invoiceDate)
	{
		this.invoiceDate = invoiceDate;
	}

	public BigDecimal  getGoodsCost()
	{
		return goodsCost;
	}

	public void        setGoodsCost(BigDecimal goodsCost)
	{
		this.goodsCost = goodsCost;
	}

	public int         getGoodsNumber()
	{
		return goodsNumber;
	}

	public void        setGoodsNumber(int goodsNumber)
	{
		this.goodsNumber = goodsNumber;
	}


	/* public: InvoiceView (support) interface */

	public InvoiceView init(Object obj)
	{
		if(obj instanceof Object[])
			for(Object o : (Object[])obj)
				this.init(o);

		if(obj instanceof Invoice)
			this.init((Invoice) obj);

		return this;
	}

	public InvoiceView init(Invoice i)
	{
		//~: primary key
		if(i.getPrimaryKey() != null)
			this.objectKey = i.getPrimaryKey().toString();

		//~: domain
		if(i.getDomain() != null)
			this.domain = i.getDomain().getPrimaryKey();

		//~: type key
		this.invoiceType = (i.getInvoiceType() == null)?(null):
		  (i.getInvoiceType().getPrimaryKey());

		//~: type name
		this.invoiceTypeName = Invoices.getInvoiceTypeName(i);

		//~: sub-type
		this.subType = i.getInvoiceData().getSubType();

		//~: state name
		this.invoiceStateName = Invoices.getInvoiceStateName(i);

		//~: code
		this.invoiceCode = i.getCode();

		//~: date
		this.invoiceDate = i.getInvoiceDate();

		//~: goods cost
		if(Invoices.getInvoiceGoodsCost(i) != null)
			this.goodsCost = Invoices.getInvoiceGoodsCost(i).
			  setScale(2, BigDecimal.ROUND_HALF_EVEN);

		//~: goods number
		this.goodsNumber = i.getInvoiceData().getGoods().size();

		return this;
	}


	/* private: the view state */

	private String     objectKey;
	private Long       domain;
	private Long       invoiceType;
	private Character  subType;
	private String     invoiceTypeName;
	private String     invoiceStateName;
	private String     invoiceCode;
	private Date       invoiceDate;
	private BigDecimal goodsCost;
	private int        goodsNumber;

}