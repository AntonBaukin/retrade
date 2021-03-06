package com.tverts.retrade.domain.prices;

/* Java */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade domain (goods) */

import com.tverts.retrade.domain.goods.GetGoods;
import com.tverts.retrade.domain.goods.GoodUnit;
import com.tverts.retrade.domain.goods.Goods;
import com.tverts.retrade.domain.goods.MeasureUnit;

/* com.tverts: support */

import com.tverts.support.jaxb.DateTimeAdapter;


/**
 * Stores read-only data of a
 * Price Change Document position.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "price-change")
@XmlType(name = "price-change-view")
public class PriceChangeView implements Serializable
{
	public static final long serialVersionUID = 20140803L;


	/* Price Change View */

	/**
	 * Stores the key of the {@link PriceChange} instance.
	 */
	public Long getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
	}

	/**
	 * Index within the Price Change Document starting with 1.
	 */
	public int getDocumentIndex()
	{
		return documentIndex;
	}

	private int documentIndex;

	public void setDocumentIndex(int documentIndex)
	{
		this.documentIndex = documentIndex;
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

	public BigDecimal  getPriceOld()
	{
		return priceOld;
	}

	private BigDecimal priceOld;

	public void setPriceOld(BigDecimal priceOld)
	{
		this.priceOld = priceOld;
	}

	public BigDecimal getPriceCur()
	{
		return priceCur;
	}

	private BigDecimal priceCur;

	public void setPriceCur(BigDecimal p)
	{
		if(p != null)
			p = p.setScale(2);
		this.priceCur = p;
	}

	public BigDecimal getPriceNew()
	{
		return priceNew;
	}

	private BigDecimal priceNew;

	public void setPriceNew(BigDecimal p)
	{
		if(p != null)
			p = p.setScale(2);
		this.priceNew = p;
	}

	public BigDecimal getRestCost()
	{
		return restCost;
	}

	private BigDecimal restCost;

	public void setRestCost(BigDecimal restCost)
	{
		this.restCost = restCost;
	}

	public Long getGoodKey()
	{
		return goodKey;
	}

	private Long goodKey;

	public void setGoodKey(Long goodKey)
	{
		this.goodKey = goodKey;
	}

	public String getGoodCode()
	{
		return goodCode;
	}

	private String goodCode;

	public void setGoodCode(String goodCode)
	{
		this.goodCode = goodCode;
	}

	public String getGoodName()
	{
		return goodName;
	}

	private String goodName;

	public void setGoodName(String goodName)
	{
		this.goodName = goodName;
	}

	public String getGoodGroup()
	{
		return goodGroup;
	}

	private String goodGroup;

	public void setGoodGroup(String goodGroup)
	{
		this.goodGroup = goodGroup;
	}

	public String getMeasureName()
	{
		return measureName;
	}

	private String measureName;

	public void setMeasureName(String measureName)
	{
		this.measureName = measureName;
	}

	public Long getPriceListOld()
	{
		return priceListOld;
	}

	private Long priceListOld;

	public void setPriceListOld(Long priceListOld)
	{
		this.priceListOld = priceListOld;
	}

	public String getPriceListOldCode()
	{
		return priceListOldCode;
	}

	private String priceListOldCode;

	public void setPriceListOldCode(String priceListOldCode)
	{
		this.priceListOldCode = priceListOldCode;
	}

	public String getPriceListOldName()
	{
		return priceListOldName;
	}

	private String priceListOldName;

	public void setPriceListOldName(String priceListOldName)
	{
		this.priceListOldName = priceListOldName;
	}

	public Long getPriceListNew()
	{
		return priceListNew;
	}

	private Long priceListNew;

	public void setPriceListNew(Long priceListNew)
	{
		this.priceListNew = priceListNew;
	}

	public String getPriceListNewCode()
	{
		return priceListNewCode;
	}

	private String priceListNewCode;

	public void setPriceListNewCode(String priceListNewCode)
	{
		this.priceListNewCode = priceListNewCode;
	}

	public String getPriceListNewName()
	{
		return priceListNewName;
	}

	private String priceListNewName;

	public void setPriceListNewName(String priceListNewName)
	{
		this.priceListNewName = priceListNewName;
	}


	/* Initialization */

	public PriceChangeView init(Object obj)
	{
		if(obj instanceof PriceChange)
			return this.init((PriceChange)obj);

		if(obj instanceof GoodPrice)
			return this.init((GoodPrice)obj);

		if(obj instanceof GoodUnit)
			return this.init((GoodUnit)obj);

		if(obj instanceof MeasureUnit)
			return this.init((MeasureUnit)obj);

		if(obj instanceof AggrValue)
			return this.initAggrValue((AggrValue)obj);

		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		return this;
	}

	public PriceChangeView init(PriceChange pc)
	{
		//~: price change pk
		objectKey = pc.getPrimaryKey();

		//~: change time
		changeTime = pc.getChangeTime();

		//~: price old
		priceOld = pc.getPriceOld();

		//~: price new
		priceNew = pc.getPriceNew();

		return this;
	}

	public PriceChangeView init(GoodPrice gp)
	{
		//~: current price
		priceCur = gp.getPrice();

		return this;
	}

	public PriceChangeView init(GoodUnit gu)
	{
		//~: good unit primary key
		goodKey = gu.getPrimaryKey();

		//~: good code
		goodCode = gu.getCode();

		//~: good name
		goodName = gu.getName();

		//~: good group
		goodGroup = bean(GetGoods.class).
		  getAttrString(gu, Goods.AT_GROUP);

		return this;
	}

	public PriceChangeView init(MeasureUnit mu)
	{
		//~: good name
		measureName = mu.getCode();

		return this;
	}

	public PriceChangeView initAggrValue(AggrValue av)
	{
		//?: {rest cost value}
		if(Goods.aggrTypeRestCost().equals(av.getAggrType()))
			return initCost(av);

		return this;
	}

	public PriceChangeView initCost(AggrValue av)
	{
		setRestCost(Goods.aggrValueRestCost(av));

		return this;
	}

	public PriceChangeView init(int documentIndex)
	{
		this.documentIndex = documentIndex;
		return this;
	}

	public PriceChangeView initListOld(PriceListEntity pl)
	{
		priceListOld     = pl.getPrimaryKey();
		priceListOldCode = pl.getCode();
		priceListOldName = pl.getName();

		return this;
	}

	public PriceChangeView initListNew(PriceListEntity pl)
	{
		priceListNew     = pl.getPrimaryKey();
		priceListNewCode = pl.getCode();
		priceListNewName = pl.getName();

		return this;
	}
}