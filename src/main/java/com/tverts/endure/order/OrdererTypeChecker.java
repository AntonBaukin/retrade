package com.tverts.endure.order;

/* com.tverts: support  */

import static com.tverts.support.SU.s2s;


/**
 * This check strategy has four optional parameters:
 *
 *  · the Unity Type of order item;
 *  · the Unity Type of order owner.
 *
 * If none of them is defined, the checked denies all
 * the requests. If at least one is, and the type
 * matches, the checker transits the request to the
 * order wrapped.
 *
 * @author anton.baukin@gmail.com
 */
public class OrdererTypeChecker extends OrdererChecker
{
	/* public: OrdererTypeChecker bean interface */

	public Class  getOrderTypeClass()
	{
		return orderTypeClass;
	}

	public void   setOrderTypeClass(Class orderTypeClass)
	{
		this.orderTypeClass = orderTypeClass;
	}

	public String getOrderTypeName()
	{
		return orderTypeName;
	}

	public void   setOrderTypeName(String orderTypeName)
	{
		this.orderTypeName = s2s(orderTypeName);
	}

	public Class  getOwnerTypeClass()
	{
		return ownerTypeClass;
	}

	public void   setOwnerTypeClass(Class ownerTypeClass)
	{
		this.ownerTypeClass = ownerTypeClass;
	}

	public String getOwnerTypeName()
	{
		return ownerTypeName;
	}

	public void   setOwnerTypeName(String ownerTypeName)
	{
		this.ownerTypeName = s2s(ownerTypeName);
	}


	/* protected: OrdererBase interface */

	protected boolean isThatRequest(OrderRequest request)
	{
		Boolean order = isThatOrderType(request);
		Boolean owner = isThatOwnerType(request);

		//?: {has both results undefined} say no
		if((order == null) & (owner == null))
			return false;

		//?: {at least one says no} say so
		if(Boolean.FALSE.equals(order) || Boolean.FALSE.equals(owner))
			return false;

		//!: at least one said yes
		return true;
	}

	protected Boolean isThatOrderType(OrderRequest request)
	{
		Class  c = getOrderTypeClass();
		String n = getOrderTypeName();

		if((c == null) | (n == null))
			return null;

		return isOrderType(request, c, n);
	}

	protected Boolean isThatOwnerType(OrderRequest request)
	{
		Class  c = getOwnerTypeClass();
		String n = getOwnerTypeName();

		if((c == null) | (n == null))
			return null;

		return isOwnerType(request, c, n);
	}


	/* private: check parameters */

	protected Class  orderTypeClass;
	protected String orderTypeName;

	protected Class  ownerTypeClass;
	protected String ownerTypeName;
}