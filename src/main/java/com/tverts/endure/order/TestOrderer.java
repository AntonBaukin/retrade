package com.tverts.endure.order;

/**
 * This test ordering strategy is for {@link ShuntOrdering}
 * self shunt unit.
 *
 * It works only for {@link ExternalOrder} instances
 * having 'Test: Ordering' order type. (The owner Test Domain.)
 *
 *
 * @author anton.baukin@gmail.com
 */
public class TestOrderer extends OrdererBase
{
	/* protected: OrdererBase interface */

	protected boolean isThatRequest(OrderRequest request)
	{
		return isAnInstance(request, ExternalOrder.class) &&
		  isType(request, ExternalOrder.class, "Test: Ordering");
	}

	protected void    order(OrderRequest request)
	{

	}
}