package com.tverts.endure.order;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Point to access {@link Orderer} strategies.
 *
 * @author anton.baukin@gmail.com
 */
public class OrderPoint
{
	/* public: Singleton */

	public static final OrderPoint INSTANCE =
	  new OrderPoint();

	public static OrderPoint getInstance()
	{
		return INSTANCE;
	}

	private OrderPoint()
	{}


	/* Order Point */

	public static void order(OrderRequest request)
	{
		INSTANCE.setOrderIndex(request);
	}

	public static void orderBefore(OrderIndex instance, OrderIndex reference)
	{
		INSTANCE.setOrderIndex(new OrderRequest(
		  instance, reference).setBeforeAfter(false));
	}

	public static void orderAfter(OrderIndex instance, OrderIndex reference)
	{
		INSTANCE.setOrderIndex(new OrderRequest(
		  instance, reference).setBeforeAfter(true));
	}

	public void        setOrderIndex(OrderRequest request)
	{
		EX.assertn(request);

		//~: invoke the root orderer
		getOrderer().setOrderIndex(request);

		//?: {no strategy found}
		EX.assertx(request.isComplete(), "No order index strategy ",
		  "found for the request: ", request.toString());
	}


	/* Order Point (bean) */

	public Orderer getOrderer()
	{
		return orderer;
	}

	private volatile Orderer orderer;

	public void setOrderer(Orderer orderer)
	{
		this.orderer = EX.assertn(orderer);
	}
}