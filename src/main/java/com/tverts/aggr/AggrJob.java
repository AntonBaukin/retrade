package com.tverts.aggr;

/* standard Java classes */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public AggrValue getAggrValue()
	{
		return aggrValue;
	}

	public AggrJob   setAggrValue(AggrValue aggrValue)
	{
		this.aggrValue = aggrValue;
		return this;
	}

	public int       size()
	{
		return (aggrTasks == null)?(0):(aggrTasks.length);
	}

	public AggrTask  task(int i)
	{
		return aggrTasks[i];
	}

	public String    error(int i)
	{
		return (errors == null)?(null):(errors[i]);
	}

	public AggrJob   error(int i, String error)
	{
		if((errors == null) && (aggrTasks == null))
			throw new IllegalStateException();

		if(errors == null)
			errors = new String[aggrTasks.length];

		errors[i] = error;
		return this;
	}

	public AggrJob   setTasks(List<AggrTask> tasks)
	{
		this.aggrTasks = tasks.toArray(new AggrTask[tasks.size()]);
		return this;
	}

	public AggrJob   setRequests(List<AggrRequest> requests)
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

	public AggrJob   setRequest(AggrRequest request)
	{
		return setRequests(Collections.singletonList(request));
	}


	/* private: the job components */

	private AggrValue  aggrValue;
	private AggrTask[] aggrTasks;
	private String[]   errors;
}