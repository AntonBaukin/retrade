package com.tverts.aggr;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxPoint;

/* com.tverts: spring */

import static com.tverts.spring.SpringPoint.bean;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;

/* com.tverts: support */

import com.tverts.support.EX;



/**
 * Facade point to post aggregation requests into the system.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrPoint
{
	/* Aggregation Point Singleton */

	public static AggrPoint getInstance()
	{
		return INSTANCE;
	}

	public static final AggrPoint INSTANCE =
	  new AggrPoint();

	protected AggrPoint()
	{}


	/* Aggregation Point (bean) */

	protected Aggregator aggregator = new AggregatorsRoot();

	public void setAggregator(Aggregator aggregator)
	{
		this.aggregator = EX.assertn(aggregator);
	}


	/* Aggregation Point (aggregation) */

	public static void aggr(AggrRequest request)
	{
		INSTANCE.postAggrRequest(request);
	}


	/* public: aggregation request post routines */

	/**
	 * This call saves the aggregation request given to
	 * the database for further background execution by
	 * the aggregation service.
	 *
	 * Note that the service may be not installed in
	 * this module, this does no matter.
	 */
	public void postAggrRequest(AggrRequest request)
	{
		EX.assertn(request.getAggrValue());
		EX.assertn(request.getAggrTask());

		//HINT: aggregation requests for test purposes
		//  still have positive primary key!

		//~: set the primary key
		HiberPoint.setPrimaryKey(TxPoint.txSession(), request);

		//!: do save the object
		TxPoint.txSession().save(request);

		//TODO notify aggregation service on request
	}
}