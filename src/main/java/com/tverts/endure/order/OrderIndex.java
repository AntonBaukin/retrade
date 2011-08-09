package com.tverts.endure.order;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * TODO comment OrderIndex
 *
 * @author anton.baukin@gmail.com
 */
public interface OrderIndex
{
	/* public: OrderIndex interface */

	public Long      getPrimaryKey();

	public Unity     getOrderOwner();

	/**
	 * Returns the type of the order. Each order owner
	 * may have several lines of order, each having
	 * it's own distinct type.
	 *
	 * Note that it is allows the type to be undefined!
	 */
	public UnityType getOrderType();

	/**
	 * Returns the order index. Note that it may be
	 * (temporary) undefined!
	 */
	public Long      getOrderIndex();

	public void      setOrderIndex(Long oi);
}