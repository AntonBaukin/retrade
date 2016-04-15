package com.tverts.endure.aggr;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;

/* com.tverts: support */

import static com.tverts.support.OU.eqcls;


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

	public Long      getSourceKey()
	{
		return sourceKey;
	}

	public void      setSourceKey(Long id)
	{
		this.sourceKey = id;
	}


	/* public: OrderIndex interface */

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

	public void      setHistoryIndex(Long historyIndex)
	{
		this.historyIndex = historyIndex;
	}


	/* public: Object interface */

	public boolean   equals(Object o)
	{
		if(this == o) return true;
		if(!eqcls(this, o)) return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((AggrItemBase)o).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int       hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}


	/* persisted attributes */

	private Long      primaryKey;
	private AggrValue aggrValue;
	private Long      sourceKey;


	/* persisted attributes: order indices */

	private Long      orderIndex;
	private Long      historyIndex;
}