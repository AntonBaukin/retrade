package com.tverts.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;

/* com.tverts: aggregation */

import com.tverts.endure.aggr.AggrTask;

/* com.tverts: support */

import com.tverts.support.EX;


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
			//?: {processed aggregation} mark the job complete
			if(aggregateJob(job))
				job.complete(true);
		}
	}


	/* protected: aggregation */

	protected abstract void aggregate(AggrTask task)
	  throws Throwable;

	/**
	 * Aggregates al the job running the tasks separately.
	 * Returns true if the task was completed.
	 */
	protected boolean       aggregateJob(AggrJob job)
	{
		//~: run all the tasks separately
		for(int i = 0;(i < job.size());i++) try
		{
			//?: {got empty task} skip it
			if(job.task(i) != null)
				continue;

			//!: aggregate the task given
			aggregate(job.task(i));
		}
		catch(Throwable e)
		{
			Boolean r = handleTaskError(job, i, e);

			//?: {the task had ordered to stop}
			if(r != null)
				return r;
		}

		return true;
	}

	/**
	 * Handles the error occurred while aggregating. If the result is
	 * undefined, the job processor will ignore it and continue with
	 * the next task.
	 *
	 * The defined result is returned as the result of the job processor
	 * {@link #aggregateJob(AggrJob)}.
	 *
	 * Note that false result means the it is possible that some else
	 * aggregator will take this job from it's beginning task, and this
	 * aggregator must undo all the previous work.
	 */
	protected Boolean       handleTaskError(AggrJob job, int i, Throwable e)
	{
		job.error(i, EX.print(e));
		return null;
	}

	protected boolean       isJobSupported(AggrJob job)
	{
		return getSupportedTasks().containsAll(job.tasksClasses());
	}

	/**
	 * Invoke this method from an aggregator constructor to define
	 * the set of aggregation tasks classes that aggregator supports.
	 */
	protected void          setSupportedTasks(Class... tasks)
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