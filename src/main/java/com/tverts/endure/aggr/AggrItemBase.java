package com.tverts.endure.aggr;

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

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
	/* public: NumericIdentity interface */

	public Long      getPrimaryKey()
	{
		return primaryKey;
	}

	public void      setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: AggrItem interface */

	public AggrValue getAggrValue()
	{
		return aggrValue;
	}

	public void      setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
	}

	public boolean   isHistorical()
	{
		return (getHistoryIndex() != null);
	}

	public void      setHistorical(boolean h)
	{
		this.historyIndex = (h)?(getOrderIndex()):(null);
	}


	/* public: OrderIndex interface */

	public Unity     getOrderOwner()
	{
		return (getAggrValue() == null)?(null)
		  :(getAggrValue().getOwner());
	}

	public UnityType getOrderType()
	{
		return null;
	}

	public Long      getOrderIndex()
	{
		return this.orderIndex;
	}

	public void      setOrderIndex(Long oi)
	{
		this.orderIndex = oi;
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

	public void      setHistoryIndex(Long historyIndex)
	{
		this.historyIndex = historyIndex;
	}


	/* persisted attributes */

	private Long      primaryKey;
	private AggrValue aggrValue;

	/* persisted attributes: order indices */

	private Long      orderIndex;
	private Long      historyIndex;
}