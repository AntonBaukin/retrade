package com.tverts.aggr;

/* standard Java classes */

import java.util.Collections;
import java.util.List;


/**
 * Composite aggregator that holds a collection of
 * links to actual aggregators that are invoked.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class      AggregatorsRoot
       extends    AggregationSystem
       implements AggregatorReference
{
	/* public: AggregatorReference interface */

	public List<Aggregator> dereferObjects()
	{
		return Collections.<Aggregator> singletonList(this);
	}


	/* public: Aggregator interface */

	public void aggregate(AggrJob job)
	{
		if((getReference() == null) || job.complete())
			return;

		//~: set the transaction context
		installAggrTx(job);

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


	/* public: AggregatorsRoot (bean) interface */

	public AggregatorReference getReference()
	{
		return reference;
	}

	public void setReference(AggregatorReference reference)
	{
		this.reference = reference;
	}


	/* private: reference to the aggregators */

	private AggregatorReference reference;
}