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

	public Unity     getOrderOwner();

	public UnityType getOrderType();

	public long      getOrderIndex();

	public void      setOrderIndex(long oi);
}