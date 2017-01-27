package com.tverts.aggr;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* com.tverts: endure (aggregation) */

import com.tverts.endure.aggr.AggrRequest;
import com.tverts.endure.aggr.AggrTask;
import com.tverts.endure.aggr.AggrValue;
import com.tverts.system.tx.Tx;

/* com.tverts: support */

import static com.tverts.support.AU.indexOfRef;


/**
 * Record of aggregation strategy invocation. Represents
 * a collection of aggregation requests that may be
 * safely executed on the level of the database.
 *
 * Each aggregation request (task) forming the job
 * must be for the same aggregated value.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class AggrJob
{
	/* public: AggrJob interface */

	public AggrValue  aggrValue()
	{
		return aggrValue;
	}

	public AggrJob    aggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
		return this;
	}

	public Tx aggrTx()
	{
		return aggrTx;
	}

	public AggrJob    aggrTx(Tx tx)
	{
		this.aggrTx = tx;
		return this;
	}

	public int        size()
	{
		return (aggrTasks == null)?(0):(aggrTasks.length);
	}

	public AggrTask   task(int i)
	{
		return ((i < 0) | (i >= aggrTasks.length))?(null):(aggrTasks[i]);
	}

	public AggrJob    tasks(List<AggrTask> tasks)
	{
		this.aggrTasks    = tasks.toArray(new AggrTask[tasks.size()]);
		this.tasksClasses = new HashSet<Class>(3);

		for(AggrTask task : tasks)
			this.tasksClasses.add(task.getClass());

		return this;
	}

	public AggrJob    requests(List<AggrRequest> requests)
	{
		ArrayList<AggrTask> tasks = new ArrayList<AggrTask>(requests.size());

		for(AggrRequest request : requests)
		{
			AggrValue value = request.getAggrValue();
			AggrTask  task  = request.getAggrTask();

			//?: {the aggregated value is not defined}
			if((value == null) | (task == null))
				throw new IllegalArgumentException();

			//~: assign aggregated value to this job
			if(aggrValue() == null)
				aggrValue(value);

			//?: {aggregated value differs}
			if(!aggrValue().equals(value))
				throw new IllegalStateException();

			//!: add the task
			tasks.add(task);
		}

		return this.tasks(tasks);
	}

	public AggrJob    request(AggrRequest request)
	{
		return requests(Collections.singletonList(request));
	}

	public Set<Class> tasksClasses()
	{
		return tasksClasses;
	}

	public boolean    error()
	{
		if(errors == null)
			return false;

		for(String error : errors)
			if(error != null)
				return true;

		return false;
	}

	public String     error(int i)
	{
		return (errors == null)?(null):
		  ((i < 0) | (i >= errors.length))?(null):(errors[i]);
	}

	public String     error(AggrTask task)
	{
		return error(indexOfRef(aggrTasks, task));
	}

	public AggrJob    error(int i, String error)
	{
		if((errors == null) && (aggrTasks == null))
			throw new IllegalStateException();

		if(errors == null)
			errors = new String[aggrTasks.length];

		if((i >= 0) & (i < errors.length))
			errors[i] = error;

		return this;
	}

	public AggrJob    error(AggrTask task, String error)
	{
		return error(indexOfRef(aggrTasks, task), error);
	}

	public boolean    complete()
	{
		return complete;
	}

	public AggrJob    complete(boolean complete)
	{
		this.complete = complete;
		return this;
	}


	/* public: Object interface */

	public String     toString()
	{
		return String.format(
		  "Aggregation Job for AggrValue [%d] with aggregation tasks of classes: %s",
		  aggrValue().getPrimaryKey(), tasksClasses().toString());
	}


	/* private: the job components */

	private AggrValue  aggrValue;
	private Tx     aggrTx;
	private AggrTask[] aggrTasks;
	private Set<Class> tasksClasses;
	private String[]   errors;
	private boolean    complete;
}