package com.tverts.aggr;

/* Spring Framework */

import org.springframework.transaction.annotation.Transactional;

/* com.tverts: hibery */

import static com.tverts.hibery.HiberPoint.session;
import static com.tverts.hibery.HiberPoint.setPrimaryKey;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;


/**
 * Facade point to post aggregation requests into the system.
 *
 * @author anton.baukin@gmail.com
 */
public class AggrPoint
{
	/* AggrPoint Singleton */

	public static AggrPoint getInstance()
	{
		return INSTANCE;
	}

	private static final AggrPoint INSTANCE =
	  new AggrPoint();

	protected AggrPoint()
	{}


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
	@Transactional
	public void postAggrRequest(AggrRequest request)
	{
		if(request.getAggrValue() == null)
			throw new IllegalArgumentException();

		if(request.getAggrTask() == null)
			throw new IllegalArgumentException();

		//HINT: aggregation requests for test purposes
		//  still have positive primary key!

		//~: set the primary key
		setPrimaryKey(session(), request);

		//!: do save the object
		session().save(request);
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
	public void runAggrRequest(AggrRequest request)
	{
		this.runAggrRequest(new AggrJob().setRequest(request));
	}

	/**
	 * Does synchronous execution of the aggregation job.
	 */
	public void runAggrRequest(AggrJob job)
	{
		//!: invoke the root aggregator
		aggregator.aggregate(job);

		//?: {the job was not completed}
		if(!job.complete()) throw new IllegalStateException(
		  "Aggregation Subsystem was unable to find strategy able to execute " +
		  job.toString() + "!");
	}

	public void setAggregator(Aggregator aggregator)
	{
		if(aggregator == null) throw new IllegalArgumentException();
		this.aggregator = aggregator;
	}

	/* protected: access aggregator */

	protected Aggregator getAggregator()
	{
		return this.aggregator;
	}


	/* private: the aggregator strategy reference */

	private Aggregator aggregator = new AggregatorsRoot();
}