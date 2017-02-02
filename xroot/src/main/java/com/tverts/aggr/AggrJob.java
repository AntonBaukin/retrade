package com.tverts.aggr;

/* Java */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrTask;
import com.tverts.endure.aggr.AggrValue;

/* com.tverts: system (transactions) */

import com.tverts.system.tx.Tx;

/* com.tverts: support */

import com.tverts.support.AU;
import com.tverts.support.CMP;
import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Record of aggregation strategy invocation.
 *
 * Represents a collection of aggregation requests
 * that may be executed in a bunch.
 *
 * Each aggregation request (task) forming the job
 * is for the same aggregated value.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrJob
{
	public AggrJob(AggrValue aggrValue)
	{
		this.aggrValue = EX.assertn(aggrValue);
	}

	/**
	 * Aggregated value of this job.
	 */
	public final AggrValue aggrValue;


	/* Static Constructors */

	public static AggrJob create(AggrRequest request)
	{
		return create(Collections.singletonList(request));
	}

	public static AggrJob create(List<AggrRequest> requests)
	{
		//?: {are requests defined}
		EX.asserte(requests);
		requests.forEach(r -> EX.assertn(r));

		//~: create job by the first task
		AggrJob job = new AggrJob(requests.get(0).getAggrValue());

		//=: target tasks array
		job.tasks = new AggrTask[requests.size()];

		//c: for each request create the task
		for(int i = 0;(i < requests.size());i++)
		{
			AggrRequest r = requests.get(i);

			//?: {is value assigned}
			EX.assertn(r.getAggrValue());

			//?: {is task assigned}
			EX.assertn(r.getAggrTask());

			//?: {is the same value}
			EX.assertx(CMP.eq(job.aggrValue, r.getAggrValue()));

			//=: task of the request
			job.tasks[i] = r.getAggrTask();
		}

		return job;
	}


	/* Aggregation Job (access) */

	/**
	 * Transactional context of the aggregation.
	 */
	public Tx aggrTx;

	public AggrJob  aggrTx(Tx tx)
	{
		this.aggrTx = tx;
		return this;
	}

	public int      size()
	{
		return (tasks == null)?(0):(tasks.length);
	}

	public AggrTask task(int i)
	{
		return ((i < 0) | (i >= tasks.length))?(null):(tasks[i]);
	}

	protected AggrTask[] tasks;

	/**
	 * Resulting set contains class of each task.
	 */
	public Set<Class<? extends AggrTask>> classes()
	{
		if(tasks == null || tasks.length == 0)
			return Collections.emptySet();

		if(classes != null)
			return classes;

		Set<Class<? extends AggrTask>> cs = new HashSet<>(1);
		for(AggrTask task : tasks) cs.add(task.getClass());

		return classes = Collections.unmodifiableSet(cs);
	}

	protected Set<Class<? extends AggrTask>> classes;


	/* Aggregation Job (errors & completion) */

	public boolean complete()
	{
		return complete;
	}

	private boolean complete;

	public AggrJob complete(boolean complete)
	{
		this.complete = complete;
		return this;
	}

	public boolean  error()
	{
		return errors.isEmpty();
	}

	protected final Map<Integer, String> errors = new HashMap<>();

	public String   error(int i)
	{
		return errors.get(i);
	}

	public String   error(AggrTask task)
	{
		return error(AU.indexOfRef(tasks, task));
	}

	public AggrJob  error(int i, String error)
	{
		EX.assertx(i >= 0 && i < tasks.length);

		if(error == null)
			errors.remove(i);
		else
			errors.put(i, error);

		return this;
	}

	public AggrJob  error(AggrTask task, String error)
	{
		return error(AU.indexOfRef(tasks, task), error);
	}


	/* Object */

	public String toString()
	{
		return SU.cats(
		  "Aggregation job for value [", aggrValue.getPrimaryKey(),
		  "] with tasks {", SU.scats(", ", classes()), "}"
		);
	}
}