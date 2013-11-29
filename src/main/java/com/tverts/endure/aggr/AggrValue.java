package com.tverts.endure.aggr;

/* standard Java classes */

import java.math.BigDecimal;

/* com.tverts: endure core */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.core.Entity;


/**
 * Aggregated Value is a data facade standing for
 * a collection of aggregation components referring it.
 *
 * COMMENT complete comments on AggrValue
 *
 * @author anton.baukin@gmail.com
 */
public class AggrValue extends Entity
{
	/* public: AggrValue bean interface */

	public Unity      getOwner()
	{
		return owner;
	}

	public void       setOwner(Unity owner)
	{
		this.owner = owner;
	}

	/**
	 * This read-only reference to the Unity Type of
	 * this Aggregated Value. It is the same the it's
	 * Unity' type.
	 */
	public UnityType  getAggrType()
	{
		return (aggrType != null)?(aggrType):
		  (getUnity() == null)?(null):(aggrType = getUnity().getUnityType());
	}

	public void       setAggrType(UnityType aggrType)
	{
		this.aggrType = aggrType;
	}

	public Long       getSelectorKey()
	{
		return selectorKey;
	}

	public void       setSelectorKey(Long id)
	{
		this.selectorKey = id;
	}

	public BigDecimal getAggrValue()
	{
		return aggrValue;
	}

	public void       setAggrValue(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrValue = v;
	}

	/**
	 * If aggregated value is not a plain decimal
	 * {@link #getAggrValue()}, but a fraction (to save
	 * the precision when divide), this values stores
	 * the denominator part of the fraction.
	 *
	 * It is always defined, by default it is one.
	 */
	public BigDecimal getAggrDenom()
	{
		return aggrDenom;
	}

	public void       setAggrDenom(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrDenom = v;
	}

	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	public void       setAggrPositive(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrPositive = v;
	}

	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	public void       setAggrNegative(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrNegative = v;
	}


	/* persisted attributes: relations */

	private Unity      owner;
	private UnityType  aggrType;
	private Long       selectorKey;


	/* persisted attributes: aggregated value (copy) */

	private BigDecimal aggrValue;
	private BigDecimal aggrDenom = BigDecimal.ONE;
	private BigDecimal aggrPositive;
	private BigDecimal aggrNegative;
}