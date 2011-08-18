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

	public AggrValue  getAggrValue()
	{
		return aggrValue;
	}

	public AggrJob    setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
		return this;
	}

	public int        size()
	{
		return (aggrTasks == null)?(0):(aggrTasks.length);
	}

	public AggrTask   task(int i)
	{
		return aggrTasks[i];
	}

	public AggrJob    setTasks(List<AggrTask> tasks)
	{
		this.aggrTasks    = tasks.toArray(new AggrTask[tasks.size()]);
		this.tasksClasses = new HashSet<Class>(3);

		for(AggrTask task : tasks)
			this.tasksClasses.add(task.getClass());

		return this;
	}

	public AggrJob    setRequests(List<AggrRequest> requests)
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
			if(getAggrValue() == null)
				setAggrValue(value);

			//?: {aggregated value differs}
			if(!getAggrValue().equals(value))
				throw new IllegalStateException();

			//!: add the task
			tasks.add(task);
		}

		return this.setTasks(tasks);
	}

	public AggrJob    setRequest(AggrRequest request)
	{
		return setRequests(Collections.singletonList(request));
	}

	public Set<Class> tasksClasses()
	{
		return tasksClasses;
	}

	public boolean    error()
	{
		if(errors == null)
			return true;

		for(String error : errors)
			if(error != null)
				return true;

		return false;
	}

	public String     error(int i)
	{
		return (errors == null)?(null):(errors[i]);
	}

	public AggrJob    error(int i, String error)
	{
		if((errors == null) && (aggrTasks == null))
			throw new IllegalStateException();

		if(errors == null)
			errors = new String[aggrTasks.length];

		errors[i] = error;
		return this;
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
		  getAggrValue().getPrimaryKey(), tasksClasses().toString());
	}


	/* private: the job components */

	private AggrValue  aggrValue;
	private AggrTask[] aggrTasks;
	private Set<Class> tasksClasses;
	private String[]   errors;
	private boolean    complete;
}