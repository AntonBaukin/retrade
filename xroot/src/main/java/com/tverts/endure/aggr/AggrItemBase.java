package com.tverts.endure.aggr;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: support */

import com.tverts.support.OU;


/**
 * Convenient abstract implementation of an aggregated
 * value' component.
 *
 * As an ordered instance it refers the aggregated value
 * as the order owner, it has order type undefined.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrItemBase implements AggrItem
{
	/* Entity with Numeric Identity */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	private Long primaryKey;

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* Aggregation Item */

	public AggrValue getAggrValue()
	{
		return aggrValue;
	}

	private AggrValue aggrValue;


	public void      setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
	}

	public boolean   isHistorical()
	{
		return (getHistoryIndex() != null);
	}

	public Long      getSourceKey()
	{
		return sourceKey;
	}

	private Long sourceKey;

	public void      setSourceKey(Long id)
	{
		this.sourceKey = id;
	}


	/* Order Index */

	public Unity     getOrderOwner()
	{
		return (getAggrValue() == null)?(null)
		  :(getAggrValue().getUnity());
	}

	public UnityType getOrderType()
	{
		return null;
	}

	public Long      getOrderIndex()
	{
		return this.orderIndex;
	}

	private Long orderIndex;

	public void      setOrderIndex(Long oi)
	{
		this.orderIndex = oi;

		//?: {is historical component} update history index
		if(this.historyIndex != null)
			this.historyIndex = oi;
	}

	/**
	 * History index is defined only for a historical
	 * component. It MUST be always equal to the
	 * main order index.
	 */
	public Long      getHistoryIndex()
	{
		return historyIndex;
	}

	private Long historyIndex;

	public void      setHistoryIndex(Long historyIndex)
	{
		this.historyIndex = historyIndex;
	}


	/* Object */

	public boolean   equals(Object o)
	{
		return OU.eq(this, o);
	}

	public int       hashCode()
	{
		Long k0 = this.getPrimaryKey();
		return (k0 == null)?(0):(k0.hashCode());
	}
}