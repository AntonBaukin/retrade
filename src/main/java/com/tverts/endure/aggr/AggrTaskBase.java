package com.tverts.endure.aggr;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;


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

	/**
	 * Sets the order reference ID and before-after flag
	 * by the closest reference on the left. If it is
	 * not defined, tells to insert as the first.
	 */
	public void    setOrderRefLeft(NumericIdentity ref)
	{
		if(ref == null)
			setBeforeAfter(false);
		else
		{
			setOrderRefID(ref.getPrimaryKey());
			setBeforeAfter(true);
		}
	}

	/**
	 * Sets the order reference ID and before-after flag
	 * by the closest reference on the right. If it is
	 * not defined, tells to insert as the last.
	 */
	public void    setOrderRefRight(NumericIdentity ref)
	{
		if(ref == null)
			setBeforeAfter(true);
		else
		{
			setOrderRefID(ref.getPrimaryKey());
			setBeforeAfter(false);
		}
	}


	/* private: task properties */

	private Long    aggrValueID;
	private Long    sourceID;
	private Long    orderRefID;
	private boolean beforeAfter;
}