package com.tverts.endure.order;

/* com.tverts: endure */

import com.tverts.endure.Unity;
import com.tverts.endure.UnityType;


/**
 * Some entities are stored in the database with
 * the defined order: if so, they must implement
 * the interface following.
 *
 * Entities of the same class (in the same table),
 * may have different order types, that's in general
 * depends on the entity type and else matters.
 *
 * Order owner is an entity (such as a Domain)
 * that defined a namespace of the order.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface OrderIndex
{
	/* Order Index */

	public Long      getPrimaryKey();

	public Unity     getOrderOwner();

	/**
	 * Returns the type of the order. Each order owner
	 * may have several lines of order, each having
	 * it's own distinct type.
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