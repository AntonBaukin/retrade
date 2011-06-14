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
	/* public: Orderer interface */

	public void setOrderIndex(OrderRequest request);
}