package com.tverts.retrade.domain.prices;

/* Java */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * A read-only view on a price change document
 * in the table.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "reprice-document-view")
@XmlType(name = "reprice-document-view")
public class RepriceDocView implements Serializable
{
	public static final long serialVersionUID = 20140803L;


	/* public: RepriceDocView (bean) interface */

	public Long getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getCode()
	{
		return code;
	}

	private String code;

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getChangeTime()
	{
		return changeTime;
	}

	private Date changeTime;

	public void setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	public Long getPriceListKey()
	{
		return priceListKey;
	}

	private Long priceListKey;

	public void setPriceListKey(Long priceListKey)
	{
		this.priceListKey = priceListKey;
	}

	public String getPriceList()
	{
		return priceList;
	}

	private String priceList;

	public void setPriceList(String priceList)
	{
		this.priceList = priceList;
	}

	public String getChangeReason()
	{
		return changeReason;
	}

	private String changeReason;

	public void setChangeReason(String changeReason)
	{
		this.changeReason = changeReason;
	}


	/* public: initialization interface */

	public RepriceDocView init(Object obj)
	{
		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		if(obj instanceof RepriceDoc)
			return this.init((RepriceDoc)obj);

		if(obj instanceof PriceListEntity)
			return this.init((PriceListEntity)obj);

		return this;
	}

	public RepriceDocView init(RepriceDoc rd)
	{
		//~: object key
		objectKey = rd.getPrimaryKey();

		//~: code
		code = rd.getCode();

		//~: change time
		changeTime = rd.getChangeTime();

		//~: change reason
		changeReason = rd.getOx().getRemarks();

		return this;
	}

	public RepriceDocView init(PriceListEntity pl)
	{
		//=: price list key
		priceListKey = pl.getPrimaryKey();

		//=: price list name
		priceList = Prices.getPriceListFullName(pl);

		return this;
	}
}