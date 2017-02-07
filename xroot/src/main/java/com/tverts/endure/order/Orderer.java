package com.tverts.endure.order;

/**
 * Order strategy. Takes {@link OrderRequest} and updates
 * the order index of the request' target instance.
 *
 * Orderer instance is shared between the requests.
 * It must be thread-safe (reentable). It may access
 * own state (the configuration) for read only.
 *
 *
 * @author anton.baukin@gmail.com
 */
public interface Orderer
{
	/* Orderer */

	/**
	 * Selects the order index for the instance in the request.
	 *
	 * WARNING! Ordering strategy works on the database level.
	 *  It issues update HQL queries. The Hibernate Session must
	 *  be flashed and cleared before and after the processing!

	 *  You must reload the instances stored in the session
	 *  as they would become detached!
	 */
	public void setOrderIndex(OrderRequest request);
}