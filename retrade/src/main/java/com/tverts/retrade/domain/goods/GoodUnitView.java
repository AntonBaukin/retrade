package com.tverts.retrade.domain.goods;

/* Java */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* tverts.com: aggregated values */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: retrade domain (prices) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.store.TradeStore;


/**
 * Stores properties of a {@link GoodUnit}
 * and the related instances.
 *
 * @author anton.baukin@gmail.com
 */
@XmlRootElement(name = "good-unit")
@XmlType(name = "good-unit-view")
public class GoodUnitView implements Serializable
{
	public static final long serialVersionUID = 20150318L;


	/* Good Unit View */

	public Long getObjectKey()
	{
		return objectKey;
	}

	private Long objectKey;

	public void setObjectKey(Long objectKey)
	{
		this.objectKey = objectKey;
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

	public Long getMeasureKey()
	{
		return measureKey;
	}

	private Long measureKey;

	public void setMeasureKey(Long measureKey)
	{
		this.measureKey = measureKey;
	}

	public String getMeasureCode()
	{
		return measureCode;
	}

	private String measureCode;

	public void setMeasureCode(String measureCode)
	{
		this.measureCode = measureCode;
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

	@XmlElement
	public String getMeasureName()
	{
		return measureName;
	}

	private String measureName;

	@XmlElement
	public Boolean getSemiReady()
	{
		return semiReady;
	}

	private Boolean semiReady;

	@XmlElement
	public boolean isInteger()
	{
		return integer;
	}

	private boolean integer;

	@XmlElement
	public String getStoreCode()
	{
		return storeCode;
	}

	private String storeCode;


	/* Good Unit View Aggregated Values */

	public BigDecimal getRestCost()
	{
		return restCost;
	}

	private BigDecimal restCost;

	public void setRestCost(BigDecimal restCost)
	{
		this.restCost = restCost;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	private BigDecimal price;

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}

	public BigDecimal getVolume()
	{
		return volume;
	}

	private BigDecimal volume;

	public void setVolume(BigDecimal volume)
	{
		this.volume = volume;
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	private BigDecimal cost;

	public void setCost(BigDecimal cost)
	{
		if(cost != null)
			cost = cost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.cost = cost;
	}


	/* Initialization */

	public GoodUnitView init(Object obj)
	{
		if(this.objectKey == null)
			this.objectKey = obtainObjectKey(obj);

		if(obj instanceof GoodUnit)
			return this.init((GoodUnit)obj);

		if(obj instanceof AggrValue)
			return this.init((AggrValue)obj);

		if(obj instanceof GoodPrice)
			return this.init((GoodPrice)obj);

		if(obj instanceof TradeStore)
			return this.init((TradeStore)obj);

		if(obj instanceof Object[])
			obj = Arrays.asList((Object[])obj);

		if(obj instanceof Collection)
			for(Object sub : (Collection)obj)
				this.init(sub);

		return this;
	}

	public GoodUnitView init(GoodUnit gu)
	{
		if(gu == null) return this;

		//~: object key
		this.objectKey = gu.getPrimaryKey();

		//~: code
		this.goodCode = gu.getCode();

		//~: good name
		this.goodName = gu.getName();

		//~: measure key
		this.measureKey = (gu.getMeasure() == null)?(null):
		  (gu.getMeasure().getPrimaryKey());

		//~: measure unit code
		this.measureCode = (gu.getMeasure() == null)?(null):
		  (gu.getMeasure().getCode());

		//~: measure unit name
		this.measureName = (gu.getMeasure() == null)?(null):
		  (gu.getMeasure().getName());

		//~: good group
		this.goodGroup = gu.getGroup();

		//~: semi-ready flag
		if(gu.getGoodCalc() != null)
			this.semiReady = gu.getGoodCalc().isSemiReady();

		//~: is integer flag
		this.integer = (gu.getMeasure() == null)?(false):
		  (!gu.getMeasure().getOx().isFractional());

		return this;
	}

	public GoodUnitView init(AggrValue av)
	{
		//?: {rest cost value}
		if(Goods.aggrTypeRestCost().equals(av.getAggrType()))
			return initCost(av);

		//?: {in (context defined) store volume}
		if(Goods.aggrTypeStoreVolume().equals(av.getAggrType()))
			return initVolume(av.getAggrValue());

		return this;
	}

	public GoodUnitView init(GoodPrice gp)
	{
		this.price = gp.getPrice();
		return this;
	}

	public GoodUnitView initCost(AggrValue cost)
	{
		this.restCost = Goods.aggrValueRestCost(cost);
		if(this.restCost == null)
			this.volume = BigDecimal.ZERO.setScale(5);

		return this;
	}

	public GoodUnitView initCost(BigDecimal cost)
	{
		this.cost = (cost == null)?(null):
		  (cost.setScale(2, BigDecimal.ROUND_HALF_EVEN));

		return this;
	}

	public GoodUnitView initVolume(BigDecimal v)
	{
		if(v == null) v = BigDecimal.ZERO;

		if(isInteger() && (v != null) && (v.scale() != 0))
			v = v.setScale(0, BigDecimal.ROUND_FLOOR);
		if(!isInteger() && (v != null) && (v.scale() != 3))
			v = v.setScale(3, BigDecimal.ROUND_FLOOR);

		this.volume = v;
		return this;
	}

	public GoodUnitView init(TradeStore s)
	{
		this.storeCode = s.getCode();

		return this;
	}


	/* protected: init additions */

	protected Long      obtainObjectKey(Object obj)
	{
		if(obj instanceof GoodUnit)
			return ((GoodUnit)obj).getPrimaryKey();
		return null;
	}
}