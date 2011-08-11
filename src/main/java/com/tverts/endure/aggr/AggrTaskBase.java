package com.tverts.endure.aggr;

/**
 * Stores basic properties of an aggregation task.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrTaskBase implements AggrTask
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTask interface */

	public Long    getAggrValueID()
	{
		return aggrValueID;
	}

	public void    setAggrValueID(Long aggrValueID)
	{
		this.aggrValueID = aggrValueID;
	}

	public Long    getSourceID()
	{
		return sourceID;
	}

	public void    setSourceID(Long sourceID)
	{
		this.sourceID = sourceID;
	}

	public Long    getOrderRefID()
	{
		return orderRefID;
	}

	public void    setOrderRefID(Long orderRefID)
	{
		this.orderRefID = orderRefID;
	}

	public boolean isBeforeAfter()
	{
		return beforeAfter;
	}

	public void    setBeforeAfter(boolean beforeAfter)
	{
		this.beforeAfter = beforeAfter;
	}


	/* private: task properties */

	private Long    aggrValueID;
	private Long    sourceID;
	private Long    orderRefID;
	private boolean beforeAfter;
}