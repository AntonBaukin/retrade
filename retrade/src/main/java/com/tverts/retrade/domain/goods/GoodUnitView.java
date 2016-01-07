package com.tverts.retrade.domain.goods;

/* Java */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: objects */

import com.tverts.objects.XPoint;

/* tverts.com: endure (aggregated values + core) */

import com.tverts.endure.aggr.AggrValue;
import com.tverts.endure.core.AttrType;
import com.tverts.endure.core.GetUnity;
import com.tverts.endure.core.UnityAttr;

/* tverts.com: retrade api */

import com.tverts.api.core.Value;
import com.tverts.api.retrade.goods.Good;
import com.tverts.api.retrade.goods.GoodAttr;

/* com.tverts: retrade domain (prices + stores) */

import com.tverts.retrade.domain.prices.GoodPrice;
import com.tverts.retrade.domain.store.TradeStore;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Stores properties of a Good Unit
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

	@XmlElement(name = "ox-json")
	public String getOxString()
	{
		return oxString;
	}

	private String oxString;


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
		this.integer = (gu.getMeasure() != null) &&
		  !gu.getMeasure().getOx().isFractional();

		return this;
	}

	public GoodUnitView initOx(GoodUnit gu)
	{
		if(gu == null) return this;

		//~: create deep copy of the ox-good
		Good g = OU.cloneBest(gu.getOx());

		//~: load attribute values
		List<UnityAttr> atts = bean(GetUnity.class).
		  getAttrs(gu.getPrimaryKey());

		//~: by the type mapping
		HashMap<AttrType, GoodAttr> map = new HashMap<>(atts.size());
		for(UnityAttr ua : atts)
		{
			GoodAttr ga = map.get(ua.getAttrType());
			if(ga == null) map.put(ua.getAttrType(), ga =
			  OU.cloneBest((GoodAttr) ua.getAttrType().getOx()));

			//=: primary key
			ga.setPkey(ua.getPrimaryKey());

			//=: name local
			if(ga.getNameLo() == null)
				ga.setNameLo(ga.getName());

			Value v = ga.getValue();

			//?: {has no values}
			if((v == null) && (ga.getValues() == null))
				ga.setValue(Goods.value(ua));
			//?: {has several values now}
			else if(ga.getValues() != null)
				ga.getValues().add(Goods.value(ua));
			//?: {make two values}
			else
			{
				ArrayList<Value> vs = new ArrayList<>(2);
				vs.add(v); //<-- existing, first
				vs.add(Goods.value(ua));

				ga.setValue(null);
				ga.setValues(vs);
			}
		}

		//=: attribute values
		g.setAttrValues(new ArrayList<>(map.values()));

		//!: encode to JSON
		this.oxString = XPoint.json().write(g);

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