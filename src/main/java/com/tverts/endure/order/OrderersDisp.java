package com.tverts.endure.order;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * Collects a collection of references to {@link Orderer}
 * strategies. Is an Orderer itself. May be installed as
 * a root orderer into {@link OrderPoint}.
 *
 * @author anton.baukin@gmail.com
 */
public class      OrderersDisp
       implements Orderer, OrdererReference
{
	/* public: Orderer interface */

	public void setOrderIndex(OrderRequest request)
	{
		for(Orderer orderer : getOrderers())
		{
			//~: invoke the orderer
			orderer.setOrderIndex(request);

			//?: {the order task is complete} exit
			if(request.isComplete())
				return;
		}
	}


	/* public: OrdererReference interface */

	public List<Orderer> dereferObjects()
	{
		return Collections.<Orderer> singletonList(this);
	}


	/* public: OrderersDisp (bean) interface */

	public OrdererReference
	            getReference()
	{
		return reference;
	}

	public void setReference(OrdererReference reference)
	{
		this.orderers  = null;
		this.reference = reference;
	}


	/* protected: dispatching support */

	protected Orderer[] getOrderers()
	{
		//~: access the orderers accumulated
		Orderer[] res = this.orderers;
		if(res != null) return res;

		//~: derefer the orders
		OrdererReference ref = this.reference;
		List<Orderer>    lst = Collections.emptyList();

		if(ref != null)
			lst = ref.dereferObjects();

		//~: create cache store
		this.orderers = res = lst.toArray(new Orderer[lst.size()]);
		return res;
	}


	/* protected: orderers reference + accumulated */

	protected volatile OrdererReference reference;
	protected volatile Orderer[]        orderers;
}