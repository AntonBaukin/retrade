package com.tverts.endure.aggr;

/* com.tverts: endure (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.hibery.system.HiberSystem;


/**
 * Stores basic properties of an aggregation task.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggrTaskBase implements AggrTask
{
	public static final long serialVersionUID = 0L;


	/* public: AggrTask interface */

	public Long    getAggrValueKey()
	{
		return aggrValueID;
	}

	public void    setAggrValueKey(Long aggrValueID)
	{
		this.aggrValueID = aggrValueID;
	}

	public Long    getSourceKey()
	{
		return sourceID;
	}

	public void    setSourceKey(Long sourceID)
	{
		this.sourceID = sourceID;
	}

	public Long    getOrderKey()
	{
		return orderRefID;
	}

	public void    setOrderKey(Long orderRefID)
	{
		this.orderRefID = orderRefID;
	}

	public Class   getSourceClass()
	{
		return orderClass;
	}

	public void    setSourceClass(Class orderClass)
	{
		this.orderClass = orderClass;
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
			setOrderKey(ref.getPrimaryKey());
			setBeforeAfter(true);

			if(getSourceClass() == null)
				setSourceClass(HiberSystem.getInstance().findActualClass(ref));
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
			setOrderKey(ref.getPrimaryKey());
			setBeforeAfter(false);

			if(getSourceClass() == null)
				setSourceClass(HiberSystem.getInstance().findActualClass(ref));
		}
	}


	/* private: task properties */

	private Long    aggrValueID;
	private Long    sourceID;
	private Long    orderRefID;
	private Class   orderClass;
	private boolean beforeAfter;
}