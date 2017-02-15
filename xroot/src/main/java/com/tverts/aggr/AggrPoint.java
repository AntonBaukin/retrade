package com.tverts.aggr;

/* com.tverts: hibery */

import com.tverts.hibery.HiberPoint;

/* com.tverts: system (tx) */

import com.tverts.system.tx.TxBean;
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

	private static final AggrPoint INSTANCE =
	  new AggrPoint();

	protected AggrPoint()
	{}


	/* Aggregation Point (bean) */

	protected Aggregator aggregator = new AggregatorsRoot();

	public void setAggregator(Aggregator aggregator)
	{
		this.aggregator = EX.assertn(aggregator);
	}


	/* public: end user interface */

	public static void aggr(AggrRequest request)
	{
		getInstance().postAggrRequest(request);
	}

	public static void aggr(AggrRequest request, boolean synch)
	{
		if(synch)
			getInstance().runAggrRequest(request);
		else
			getInstance().postAggrRequest(request);
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
	}

	/**
	 * Does synchronous execution of the aggregation request
	 * invoking related aggregation strategy.
	 *
	 * Aggregation service does not call this method directly,
	 * but {@link #runAggrRequest(AggrJob)}. This implementation
	 * wraps the request into the job before that call.
	 *
	 * WARNING! Do not call this method until you fully sure
	 *   that concurrent requests on update the same aggregated
	 *   value are possible. Aggregated value is locked on the
	 *   database level, but still...
	 */
	protected void runAggrRequest(AggrRequest request)
	{
		this.runAggrRequest(AggrJob.create(request));
	}

	/**
	 * Does synchronous execution of the aggregation job.
	 */
	protected void runAggrRequest(AggrJob job)
	{
		//!: invoke the root aggregator
		aggregator.aggregate(job);

		//?: {the job was not completed}
		EX.assertx(job.complete(), "Aggregation Subsystem",
		  " was unable to find strategy able to execute ", job
		);
	}
}