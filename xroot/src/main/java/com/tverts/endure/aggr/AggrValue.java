package com.tverts.endure.aggr;

/* Java */

import java.math.BigDecimal;

/* com.tverts: endure core */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.endure.core.Entity;


/**
 * Aggregated Value entity is a facade for related
 * database entities being the items of the value,
 * or even data structures of else primary entities.
 * The schema of the objects depend on the type of
 * the aggregated value. Each type has own strategy
 * to handle these objects.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrValue extends Entity
{
	/* Aggregated Value (entity bean) */

	/**
	 * Unity of the instance owning this aggregated value.
	 */
	public Unity getOwner()
	{
		return owner;
	}

	private Unity owner;

	public void setOwner(Unity owner)
	{
		this.owner = owner;
	}

	/**
	 * Duplicate of Aggregate Value Unity Type reference.
	 */
	public UnityType  getAggrType()
	{
		return (aggrType != null)?(aggrType):
		  (getUnity() == null)?(null):
		  (aggrType = getUnity().getUnityType());
	}

	private UnityType aggrType;

	public void setAggrType(UnityType aggrType)
	{
		this.aggrType = aggrType;
	}

	/**
	 * Copy of the primary key of the selector of this
	 * aggregated value. Selector is optional entity to
	 * form triple link. It allows the same owner to have
	 * multiple values of the same type, each having
	 * unique selector key.
	 */
	public Long getSelectorKey()
	{
		return selectorKey;
	}

	private Long selectorKey;

	public void setSelectorKey(Long id)
	{
		this.selectorKey = id;
	}

	/**
	 * Stores current combined value of the aggregation.
	 * This value is constructed from the positive and
	 * the negative parts, thus may be negative.
	 */
	public BigDecimal getAggrValue()
	{
		return aggrValue;
	}

	private BigDecimal aggrValue;

	public void setAggrValue(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrValue = v;
	}

	/**
	 * Component of the value with the positive income.
	 * Undefined when the value is one-component.
	 */
	public BigDecimal getAggrPositive()
	{
		return aggrPositive;
	}

	private BigDecimal aggrPositive;

	public void setAggrPositive(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrPositive = v;
	}

	/**
	 * Component of the value with the negative income.
	 * Undefined when the value is one-component.
	 */
	public BigDecimal getAggrNegative()
	{
		return aggrNegative;
	}

	private BigDecimal aggrNegative;

	public void setAggrNegative(BigDecimal v)
	{
		if((v != null) && (v.scale() != 10))
			v = v.setScale(10);

		this.aggrNegative = v;
	}
}