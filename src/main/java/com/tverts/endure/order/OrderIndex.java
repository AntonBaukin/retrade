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

	public UnityType getOrderType();

	/**
	 * Returns the order index. Note that it may be
	 * (temporary) undefined!
	 */
	public Long      getOrderIndex();

	public void      setOrderIndex(Long oi);
}