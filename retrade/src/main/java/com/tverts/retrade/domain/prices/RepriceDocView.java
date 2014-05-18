package com.tverts.retrade.domain.prices;

/* standard Java classes */

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * A read-only view on a price change document
 * in the table.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement
public class RepriceDocView implements Serializable
{
	public static final long serialVersionUID = 0L;


	/* public: RepriceDocView (bean) interface */

	public Long   getObjectKey()
	{
		return objectKey;
	}

	public void   setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	public String getCode()
	{
		return code;
	}

	public void   setCode(String code)
	{
		this.code = code;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date   getChangeTime()
	{
		return changeTime;
	}

	public void   setChangeTime(Date changeTime)
	{
		this.changeTime = changeTime;
	}

	public String getPriceList()
	{
		return priceList;
	}

	public void   setPriceList(String priceList)
	{
		this.priceList = priceList;
	}

	public String getChangeReason()
	{
		return changeReason;
	}

	public void   setChangeReason(String changeReason)
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

		if(obj instanceof PriceList)
			return this.init((PriceList)obj);

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
		changeReason = rd.getChangeReason();

		return this;
	}

	public RepriceDocView init(PriceList pl)
	{
		//~: price list name
		priceList = Prices.getPriceListFullName(pl);

		return this;
	}


	/* private: price change document attributes */

	private Long   objectKey;
	private String code;
	private Date   changeTime;
	private String priceList;
	private String changeReason;
}