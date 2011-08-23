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
	 *
	 * WARNING!  Note that it is allowed the type to be
	 *  undefined. If it is so, the type is not checked
	 *  at all, and the the instances ordered may have
	 *  no order type!
	 */
	public UnityType getOrderType();

	/**
	 * Returns the order index. Note that it may be
	 * (temporary) undefined!
	 */
	public Long      getOrderIndex();

	public void      setOrderIndex(Long oi);
}