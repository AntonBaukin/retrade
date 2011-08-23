package com.tverts.aggr;

/* standard Java classes */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;

/* Hibernate Persistence Layer */

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrTask;
import com.tverts.endure.aggr.AggrValue;

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
			//?: {processed aggregation} mark the job complete
			if(aggregate(createStruct(job)))
				job.complete(true);
	}

	/* protected: aggregation */

	protected abstract void aggregateTask(AggrStruct struct)
	  throws Throwable;

	/**
	 * Aggregates al the job running the tasks separately.
	 * Returns true if the task was completed.
	 */
	protected boolean       aggregate(AggrStruct struct)
	{
		//~: check the validity of the aggregation job
		checkAggrJob(struct);

		//~: run all the tasks separately
		for(int i = 0;(i < struct.job.size());i++) try
		{
			//?: {got empty task} skip it
			if(struct.job.task(i) == null)
				continue;

			//!: aggregate the task given
			aggregateTask(struct.task(struct.job.task(i)));
		}
		catch(Throwable e)
		{
			Boolean r = handleTaskError(struct, e);

			//?: {the task is ordered to stop}
			if(r != null)
				return r;

			throw new AggrJobError(e, struct.job);
		}

		return true;
	}

	protected void          checkAggrJob(AggrStruct struct)
	{
		//?: {the aggregation value is not defined}
		if(struct.job.aggrValue() == null)
			throw new IllegalStateException(
			  "Aggregation Job is not defined!");

		//?: {the transaction context is not defined}
		if(struct.job.aggrTx() == null)
			throw new IllegalStateException(
			  "Aggregation Tx Context is not defined!");
	}

	protected AggrStruct    createStruct(AggrJob job)
	{
		return new AggrStruct(job);
	}

	/**
	 * Handles the error occurred while aggregating. If the result is
	 * undefined, the job processor will ignore it and continue with
	 * the next task.
	 *
	 * The defined result is returned as the result of the job processor
	 * {@link #aggregate(AggrStruct)}.
	 *
	 * Note that false result means the it is possible that some else
	 * aggregator will take this job from it's beginning task, and this
	 * aggregator must undo all the previous work.
	 */
	protected Boolean       handleTaskError(AggrStruct struct, Throwable e)
	{
		struct.job.error(struct.task, EX.print(e));
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


	/* public: aggregation structure */

	public class AggrStruct
	{
		/* public: constructor */

		public AggrStruct(AggrJob aggrJob)
		{
			this.job = aggrJob;
		}


		/* public: assigners */

		public AggrStruct task(AggrTask task)
		{
			this.task = task;
			return this;
		}


		/* public: structure fields */

		public final AggrJob job;
		public AggrTask      task;
	}


	/* protected: access transaction context & Hibernate session */

	protected AggrTx  tx(AggrStruct struct)
	{
		AggrTx tx = struct.job.aggrTx();

		if(tx == null) throw new IllegalStateException(
		  "Aggregator is not bound to any Transactional Context!"
		);

		return tx;
	}

	/**
	 * Returns Hibernate session of the tx context.
	 * Raises {@link IllegalStateException} if no session present.
	 */
	protected Session session(AggrStruct struct)
	{
		SessionFactory f = tx(struct).getSessionFactory();
		Session        s = (f == null)?(null):(f.getCurrentSession());

		if(s == null) throw new IllegalStateException(
		  "Aggregator got undefined Hibernate session (or factroy)!");

		return s;
	}


	/* protected: Hibernate querying */

	protected Query   Q(AggrStruct struct, String hql)
	{
		return session(struct).createQuery(hql);
	}


	/* protected: helper functions */

	protected AggrValue aggrValue(AggrStruct struct)
	{
		return struct.job.aggrValue();
	}


	/* private: aggregator support data */

	private Set<Class> supportedTasks = Collections.emptySet();
}