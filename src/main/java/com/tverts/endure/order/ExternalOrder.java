package com.tverts.endure.order;

/* com.tverts: endure */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;
import com.tverts.support.OU;


/**
 * Suppose that instances of some class are placed
 * in more than one order, or that class may not be
 * extended to implement {@link OrderIndex} directly.
 *
 * External order is an {@link OrderIndex} object
 * that represents other object in some order.
 *
 * It does not store the direct reference to that
 * object because the target class is unknown, and
 * not a {@link Unity}. That's why it ahs just a copy
 * of the target instance' primary key.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      ExternalOrder
       implements NumericIdentity, OrderIndex
{
	/* public: NumericIdentity interface */

	public Long getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}


	/* public: OrderIndex interface */

	public Unity     getOrderOwner()
	{
		return orderOwner;
	}

	public UnityType getOrderType()
	{
		return orderType;
	}

	public long      getOrderIndex()
	{
		return orderIndex;
	}

	public void      setOrderIndex(long orderIndex)
	{
		this.orderIndex = orderIndex;
	}


	/* public: ExternalOrder attributes access */

	public void setOrderOwner(Unity orderOwner)
	{
		this.orderOwner = orderOwner;
	}

	public void setOrderType(UnityType orderType)
	{
		this.orderType = orderType;
	}

	public long getOrderInstance()
	{
		return orderInstance;
	}

	public void setOrderInstance(long orderInstance)
	{
		this.orderInstance = orderInstance;
	}


	/* public: Object interface */

	public boolean equals(Object o)
	{
		if(this == o)
			return true;

		if(!(o instanceof ExternalOrder))
			return false;

		Long k0 = this.getPrimaryKey();
		Long k1 = ((ExternalOrder)o).getPrimaryKey();

		return (k0 != null) && k0.equals(k1);
	}

	public int     hashCode()
	{
		Long k0 = this.getPrimaryKey();

		return (k0 == null)?(0):(k0.hashCode());
	}

	/* private: persisted attributes */

	private Long      primaryKey;

	private Unity     orderOwner;
	private UnityType orderType;
	private long      orderIndex;
	private long      orderInstance;
}