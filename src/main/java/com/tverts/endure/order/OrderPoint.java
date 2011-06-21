package com.tverts.endure.order;

/**
 * Point to access {@link Orderer} strategies.
 *
 * @author anton.baukin@gmail.com
 */
public class OrderPoint
{
	/* public: Singleton */

	public static OrderPoint getInstance()
	{
		return INSTANCE;
	}

	private static final OrderPoint INSTANCE =
	  new OrderPoint();

	protected OrderPoint()
	{}

	/* public: OrderPoint interface */

	public void        setOrderIndex(OrderRequest request)
	{
		if(request == null) throw new IllegalArgumentException();

		//~: invoke the root orderer
		getOrderer().setOrderIndex(request);

		//?: {no strategy found}
		if(!request.isComplete()) throw new IllegalStateException(
		  "No order index strategy found for the request: " +
		  request.toString()
		);
	}

	public static void order(OrderRequest request)
	{
		getInstance().setOrderIndex(request);
	}

	public static void orderBefore(OrderIndex instance, OrderIndex reference)
	{
		getInstance().setOrderIndex(
		  new OrderRequest(instance, reference).setBeforeAfter(false));
	}

	public static void orderAfter(OrderIndex instance, OrderIndex reference)
	{
		getInstance().setOrderIndex(
		  new OrderRequest(instance, reference).setBeforeAfter(true));
	}

	/* public: OrderPoint (bean) interface */

	public Orderer getOrderer()
	{
		return orderer;
	}

	public void    setOrderer(Orderer orderer)
	{
		if(orderer == null) throw new IllegalArgumentException();
		this.orderer = orderer;
	}


	/* private: root ordering strategy */

	private volatile Orderer orderer;
}