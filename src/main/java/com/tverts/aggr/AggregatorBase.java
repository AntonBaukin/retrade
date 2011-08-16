package com.tverts.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTask;


/**
 * Basic level of {@link Aggregator} implementation.
 *
 * @author anton.baukin@gmail.com
 */
public abstract class AggregatorBase
       implements     Aggregator, AggregatorReference
{
	/* public: AggregatorReference interface */

	public List<Aggregator> dereferObjects()
	{
		return Collections.<Aggregator> singletonList(this);
	}


	/* public: Aggregator interface */

	public void aggregate(AggrJob job)
	{
		//?: {this aggregator can handle the job} invoke it
		if(isJobSupported(job))
		{
			//!: process aggregation
			process(job);

			//~: mark the job complete
			job.complete(true);
		}
	}


	/* protected: aggregation */

	protected abstract void process(AggrJob job);

	protected boolean       isJobSupported(AggrJob job)
	{
		return getSupportedTasks().containsAll(job.tasksClasses());
	}

	/**
	 * Invoke this method from an aggregator constructor to define
	 * the set of aggregation tasks classes that aggregator supports.
	 */
	protected void          setSupportedTasks(Class<? extends AggrTask>... tasks)
	{
		this.supportedTasks = Collections.unmodifiableSet(
		  new HashSet<Class>(Arrays.asList(tasks)));
	}

	protected Set<Class>    getSupportedTasks()
	{
		return this.supportedTasks;
	}


	/* private: aggregator support data */

	private Set<Class> supportedTasks = Collections.emptySet();
}