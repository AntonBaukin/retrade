package com.tverts.endure.order;

/**
 * Wraps an {@link Orderer} instance to check whether
 * the order request must be accepted or denied.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class OrdererChecker
       extends        OrdererBase
{
	/* public: OrdererChecker interface */

	public Orderer getOrderer()
	{
		return orderer;
	}

	public void    setOrderer(Orderer orderer)
	{
		this.orderer = orderer;
	}


	/* protected: OrdererBase interface */

	protected void order(OrderRequest request)
	{
		if(getOrderer() != null)
			getOrderer().setOrderIndex(request);
	}


	/* private: orderer reference */

	private Orderer orderer;
}