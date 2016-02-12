package com.tverts.retrade.domain.goods;

/* Java */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Java XML Binding */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: objects */

import com.tverts.objects.XPoint;

/* tverts.com: endure (aggregated values + core) */

import com.tverts.endure.aggr.AggrValue;
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
	public static final long serialVersionUID = 20160212L;


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

	@XmlElement(name = "good-attr")
	@XmlElementWrapper(name = "attributes")
	public List<GoodAttr> getAttrValues()
	{
		return attrValues;
	}

	private List<GoodAttr> attrValues;

	@XmlTransient
	public Map<String, Object> getAttrs()
	{
		return attrs;
	}

	private Map<String, Object> attrs = new HashMap<>();


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

	public GoodUnit     goodUnit(Object obj)
	{
		//?: {is a good}
		if(obj instanceof GoodUnit)
			return (GoodUnit)obj;

		//?: {is an array}
		if(obj instanceof Object[])
			obj = Arrays.asList((Object[]) obj);

		//?: {is a collection} search there
		if(obj instanceof Collection)
			for(Object x : (Collection)obj)
				if(x instanceof GoodUnit)
					return (GoodUnit)x;

		return null;
	}

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

		//~: semi-ready flag
		if(gu.getGoodCalc() != null)
			this.semiReady = gu.getGoodCalc().isSemiReady();

		//~: is integer flag
		this.integer = (gu.getMeasure() != null) &&
		  !gu.getMeasure().getOx().isFractional();

		return this;
	}

	@SuppressWarnings("unchecked")
	public GoodUnitView initAttrs(Object obj)
	{
		GetGoods get = bean(GetGoods.class);
		GoodUnit  gu = goodUnit(obj);

		//?: {not found it}
		if(gu == null) return this;

		//~: select good group
		this.goodGroup = (this.attrs != null)
			?((String) this.attrs.get(Goods.AT_GROUP))
			:(get.getAttrString(gu, Goods.AT_GROUP));

		return this;
	}

	@SuppressWarnings("unchecked")
	public GoodUnitView initOxAttrs(GoodUnit gu)
	{
		//~: load attribute values
		List<UnityAttr> uas = bean(GetUnity.class).
		  getAttrs(gu.getPrimaryKey());

		//~: create the type mapping
		Map<Long, GoodAttr> map = new LinkedHashMap<>();
		for(UnityAttr ua : uas)
		{
			GoodAttr ga = map.get(ua.getAttrType().getPrimaryKey());
			if(ga == null) map.put(ua.getAttrType().getPrimaryKey(),
			  ga = OU.cloneBest((GoodAttr) ua.getAttrType().getOx()));

			//=: primary key
			ga.setPkey(ua.getAttrType().getPrimaryKey());

			//=: name local
			if(ga.getNameLo() == null)
				ga.setNameLo(ga.getName());

			//=: {is taken value}
			ga.setTaken(ua.getSource() != null);

			//?: {has no values}
			Value v = ga.getValue();
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

		//=: attributes list
		this.attrValues = new ArrayList<>(map.values());

		//=: attributes mapping
		this.attrs = Goods.convert(this.attrValues);

		return this;
	}

	public GoodUnitView initOx(GoodUnit gu)
	{
		if(gu == null) return this;

		//?: {has no attributes} load them
		if(this.attrValues == null)
			this.initOxAttrs(gu);

		//~: create deep copy of the ox-good
		Good g = OU.cloneBest(gu.getOxOwn());

		//=: attribute values
		g.setAttrValues(this.attrValues);

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