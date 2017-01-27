package com.tverts.aggr;

/* Java */

import java.util.Collections;
import java.util.List;

/* com.tverts: support */

import com.tverts.support.EX;


/**
 * Composite aggregator that holds a collection of
 * links to actual aggregators that are invoked
 * in the registration order.
 *
 * @author anton.baukin@gmail.com
 */
public class      AggregatorsRoot
       extends    AggregationSystem
       implements AggregatorReference
{
	/* Aggregator Reference */

	public List<Aggregator> dereferObjects()
	{
		return Collections.<Aggregator> singletonList(this);
	}


	/* public: Aggregator interface */

	public void aggregate(AggrJob job)
	{
		EX.assertn(getReference());

		//?: {job is somehow complete}
		EX.assertx(!job.complete());

		//~: set the transaction context
		installTx(job);

		//~: invoke the aggregators referred
		for(Aggregator aggregator : getReference().dereferObjects())
		{
			aggregator.aggregate(job);

			//?: {has aggregation error}
			if(job.error())
				throw new AggrJobError(job);

			//?: {this aggregator did the job} exit
			if(job.complete())
				return;
		}
	}


	/* Aggregators Root */

	public AggregatorReference getReference()
	{
		return reference;
	}

	private AggregatorReference reference;

	public void setReference(AggregatorReference reference)
	{
		this.reference = reference;
	}
}