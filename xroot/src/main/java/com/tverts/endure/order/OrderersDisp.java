package com.tverts.endure.order;

/* Java */

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


	/* Orderer Reference */

	public List<Orderer> dereferObjects()
	{
		return Collections.<Orderer> singletonList(this);
	}


	/* Orderer Dispatcher (bean) */

	public OrdererReference getReference()
	{
		return reference;
	}

	protected volatile OrdererReference reference;

	public void setReference(OrdererReference reference)
	{
		this.orderers  = null;
		this.reference = reference;
	}


	/* protected: dispatching support */

	protected Orderer[] getOrderers()
	{
		//~: access the array accumulated
		if(this.orderers != null)
			return this.orderers;

		//~: de-refer the orders
		OrdererReference ref = this.reference;
		List<Orderer>    lst = Collections.emptyList();

		if(ref != null)
			lst = ref.dereferObjects();

		//~: create cache store
		return this.orderers =
		  lst.toArray(new Orderer[lst.size()]);
	}

	protected volatile Orderer[] orderers;
}