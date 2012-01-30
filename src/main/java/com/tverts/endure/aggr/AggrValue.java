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

	public Long       getSelectorId()
	{
		return selectorId;
	}

	public void       setSelectorId(Long id)
	{
		this.selectorId = id;
	}

	/**
	 * Copy of the primary key of the component
	 * ({@link AggrItem}) with the actual historical
	 * value of the aggregated value.
	 */
	public Long       getAggrItemId()
	{
		return aggrItemId;
	}

	public void       setAggrItemId(Long aggrItemId)
	{
		this.aggrItemId = aggrItemId;
	}

	public BigDecimal getAggrValue()
	{
		return aggrValue;
	}

	public void       setAggrValue(BigDecimal v)
	{
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
		this.aggrDenom = v;
	}


	/* persisted attributes: relations */

	private Unity      owner;
	private UnityType  aggrType;
	private Long       selectorId;
	private Long       aggrItemId;


	/* persisted attributes: aggregated value (copy) */

	private BigDecimal aggrValue;
	private BigDecimal aggrDenom = BigDecimal.ONE;
}