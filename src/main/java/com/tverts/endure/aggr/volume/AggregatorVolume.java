package com.tverts.endure.aggr.volume;

/* com.tverts: aggregation */

import com.tverts.aggr.AggregatorBase;
import com.tverts.endure.aggr.AggrTask;


/**
 * Does aggregation for {@link AggrTaskVolumeCreate} and
 * {@link AggrTaskVolumeDelete} tasks.
 *
 * @author anton.baukin@gmail.com
 */
public class AggregatorVolume extends AggregatorBase
{
	/* public: constructor */

	public AggregatorVolume()
	{
		setSupportedTasks(

		  AggrTaskVolumeCreate.class,
		  AggrTaskVolumeDelete.class
		);
	}


	/* protected: AggregatorBase (aggregation) */

	protected void aggregate(AggrTask task)
	  throws Throwable
	{

	}


	/* protected: aggregate create task */

	protected void aggregateCreate(AggrTask task)
	  throws Throwable
	{

	}


	/* protected: aggregate delete task */

	protected void aggregateDelete(AggrTask task)
	  throws Throwable
	{

	}
}