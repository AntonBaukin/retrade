package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.SU;
import com.tverts.support.jaxb.DateTimeAdapter;

/* com.tverts: retrade domain (firms) */

import com.tverts.retrade.domain.firm.Contractor;


/**
 * Read-only view on a {@link PayWay}.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "pay-way-view")
public class PayWayView implements Serializable
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getOpenTime()
	{
		return openTime;
	}

	public void setOpenTime(Date openTime)
	{
		this.openTime = openTime;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getCloseTime()
	{
		return closeTime;
	}

	public void setCloseTime(Date closeTime)
	{
		this.closeTime = closeTime;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public String getTypeFlag()
	{
		return typeFlag;
	}

	public void setTypeFlag(String typeFlag)
	{
		this.typeFlag = typeFlag;
	}

	public BigDecimal getIncome()
	{
		return income;
	}

	public void setIncome(BigDecimal v)
	{
		if(v != null) v = v.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.income = v;
	}

	public BigDecimal getExpense()
	{
		return expense;
	}

	public void setExpense(BigDecimal v)
	{
		if(v != null) v = v.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.expense = v;
	}

	@XmlElement
	public BigDecimal getBalance()
	{
		BigDecimal b = getIncome();

		if((b != null) && (getExpense() != null))
			b = b.subtract(getExpense());
		if(b != null)
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b;
	}

	public Long getContractorKey()
	{
		return contractorKey;
	}

	public void setContractorKey(Long contractorKey)
	{
		this.contractorKey = contractorKey;
	}

	public String getContractorCode()
	{
		return contractorCode;
	}

	public void setContractorCode(String contractorCode)
	{
		this.contractorCode = contractorCode;
	}

	public String getContractorName()
	{
		return contractorName;
	}

	public void setContractorName(String contractorName)
	{
		this.contractorName = contractorName;
	}


	/* public: initialization interface */

	public PayWayView init(Object obj)
	{
		if(obj instanceof PayWay)
			this.init((PayWay) obj);

		if(obj instanceof PayBank)
			this.init((PayBank) obj);

		if(obj instanceof Contractor)
			return this.init((Contractor) obj);

		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		return this;
	}

	public PayWayView init(PayWay w)
	{
		this.objectKey = w.getPrimaryKey();
		this.name = w.getName();
		this.openTime = w.getOpened();
		this.closeTime = w.getClosed();
		this.remarks = w.getRemarks();
		this.typeFlag = "" + w.getTypeFlag();

		return this;
	}

	public PayWayView init(PayBank w)
	{
		this.name = SU.cats(
		  this.name,
		  ", №", w.getRemitteeAccount(),
		  " в ", w.getBankName()
		);

		return this;
	}

	public PayWayView init(Contractor c)
	{
		this.contractorKey  = c.getPrimaryKey();
		this.contractorCode = c.getCode();
		this.contractorName = c.getName();

		return this;
	}

	public PayWayView initIncome(BigDecimal v)
	{
		setIncome(v);
		return this;
	}

	public PayWayView initExpense(BigDecimal v)
	{
		setExpense(v);
		return this;
	}


	/* account attributes */

	private Long       objectKey;
	private String     name;
	private Date       openTime;
	private Date       closeTime;
	private String     remarks;
	private String     typeFlag;
	private BigDecimal income;
	private BigDecimal expense;
	private Long       contractorKey;
	private String     contractorCode;
	private String     contractorName;
}