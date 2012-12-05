package com.tverts.exec.service;

/* standard Java classes */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* com.tverts: system (tx) */

import static com.tverts.system.tx.TxPoint.txSession;


/**
 * This execution planning service implies
 * straight strategy to selecting the
 * requests from the database.
 *
 * It takes the requests in the primary key
 * (the history) order and sends them until
 * the limit pending execution limit is reached.
 *
 *
 * @author anton.baukin@gmail.com
 */
public class   PlainExecPlanService
       extends ExecPlanServiceBase
{
	/* public: PlainExecPlanService (config) interface */

	/**
	 * The maximum number of simultaneously
	 * running execution tasks. Default is 32.
	 */
	public int  getThreadsLimit()
	{
		return threadsLimit;
	}

	public void setThreadsLimit(int threadsLimit)
	{
		if(threadsLimit <= 0) throw new IllegalArgumentException();
		this.threadsLimit = threadsLimit;
	}


	/* protected: planning strategy */

	/**
	 * Selects the next requests till the threads
	 * limit is reached. Note that this implementation
	 * always returns false because the pause is
	 * actually needed always.
	 */
	@SuppressWarnings("unchecked")
	protected boolean sendNextRequests()
	{
		List added = new ArrayList(4);
		Set  tasks;
		int  limit;

		//~: get the limit of the threads
		synchronized(threadsMutex())
		{
			tasks = new HashSet(pendingTasks);
			limit = threadsLimit - pendingTasks.size();
		}

		//?: {all the threads are busy} nothing to plan now
		if(limit <= 0)
			return true; //<-- resume with a pause

/*

select er.id from ExecRequest er where
  (er.executed = false)
order by er.id

*/

		Iterator i = txSession().createQuery(

"select er.id from ExecRequest er where\n" +
"  (er.executed = false)\n" +
"order by er.id"

		).
		  iterate();


		//HINT: as only one thread may be here, no more
		//  tasks may be added to pendingTasks set, and
		//  that set may be decreased only.


		//c: select all the requests not executed
		while((limit != 0) && i.hasNext())
		{
			Long tid = ((Number)i.next()).longValue();

			//?: {the task is already executed} skip it
			if(tasks.contains(tid))
				continue;

			//!: add it
			added.add(tid);
			limit--;
		}

		//?: {has new tasks added}
		if(!added.isEmpty())
			synchronized(threadsMutex())
			{
				pendingTasks.addAll(added);
			}

		//~: send exec tasks
		for(Object tid : added)
			sendTask((Long)tid);

		return true; //<-- achieved the limit (make a pause)
	}

	protected Object  threadsMutex()
	{
		return threadsMutex;
	}


	/* private: configuration parameters */

	private volatile int threadsLimit = 32;


	/* private: execution state */

	private final Object threadsMutex = new Object();
	private volatile Set pendingTasks = new HashSet(11);
}