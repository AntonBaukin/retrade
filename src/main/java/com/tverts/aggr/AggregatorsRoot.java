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
       implements Aggregator, AggregatorReference
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

		for(Aggregator aggregator : getReference().dereferObjects())
		{
			aggregator.aggregate(job);

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