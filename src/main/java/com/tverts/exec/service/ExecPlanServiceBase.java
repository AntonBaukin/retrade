package com.tverts.exec.service;

/* com.tverts: services */

import com.tverts.system.zservices.Event;
import com.tverts.system.zservices.ServiceBase;
import com.tverts.system.zservices.ServicesPoint;
import com.tverts.system.zservices.events.SystemReady;


/**
 * Implementation base of service to plan the
 * execution of execution requests stored
 * in the database.
 *
 *
 * @author anton.baukin@gmail.com
 */
public abstract class ExecPlanServiceBase
       extends        ServiceBase
{
	/* public: Service interface */

	public void     service(Event event)
	{
		if(event instanceof SystemReady)
			systemReady();

		if(event instanceof ExecPlanEvent)
			servicePlan();
	}

	public String[] depends()
	{
		return new String[] { getExecService() };
	}


	/* public: ExecPlanServiceBase (config) interface */

	/**
	 * Required parameter with the name of the execution
	 * service which would receive {@link ExecRunEvent}.
	 */
	public String getExecService()
	{
		return execService;
	}

	public void   setExecService(String execService)
	{
		if(execService == null) throw new IllegalArgumentException();
		this.execService = execService;
	}

	/**
	 * Wait pause (in milliseconds) to plan own execution
	 * when there are no execution requests are present.
	 * Zero is allowed. By default, 0.2 seconds.
	 */
	public long   getWaitPause()
	{
		return waitPause;
	}

	public void   setWaitPause(long waitPause)
	{
		if(waitPause < 0L) throw new IllegalArgumentException();
		this.waitPause = waitPause;
	}


	/* protected: processing database tasks  */

	/**
	 * Inspects ths databases and selects the next
	 * execution tasks to execute. Sends the event
	 * to execution service.
	 *
	 * Returns true when there are no more requests
	 * left in the database and available for
	 * execution right now.
	 *
	 * Inside this method you are thread-safe!
	 * Only one thread may enter it.
	 */
	protected abstract boolean sendNextRequests();


	/* protected: servicing infrastructure */

	protected void    systemReady()
	{
		//~: check the service
		checkService();

		//~: start planning
		planNextExecution(false);
	}

	protected void    checkService()
	{
		if(getExecService() == null)
			throw new IllegalStateException(
			  "Execution Service ID is not configured!");

		//~: error on missing service
		ServicesPoint.system().xservice(getExecService());
	}

	protected void    planNextExecution(boolean withPause)
	{
		ExecPlanEvent e = new ExecPlanEvent();
		if(withPause) e.setEventDelay(getWaitPause());

		self(e);
	}

	protected void    planNextExecution(boolean reentered, boolean empty)
	{
		planNextExecution(!reentered && empty);
	}

	protected void    servicePlan()
	{
		//?: {lock was not acquired}
		if(!enterService())
			return;

		boolean reentered;
		boolean empty;

		try
		{
			empty = sendNextRequests();
		}
		finally
		{
			//!: unlock the service
			reentered = leaveService();
		}

		//!: plan the next execution
		planNextExecution(reentered, empty);
	}

	/**
	 * Tries to enter the service. Only one thread
	 * is allowed to be in the service. False result
	 * means there is another thread owning the service.
	 * On that thread leave, it would check whether
	 * there were requests to re-enter.
	 */
	protected boolean enterService()
	{
		synchronized(enterMutex())
		{
			enterRequests++;
			if(enterRequests == 1)
				return true;
		}

		return false;
	}

	/**
	 * Invoked by the thread owned the service.
	 * Returns true when there were attempts to
	 * enter the service by other threads.
	 */
	protected boolean leaveService()
	{
		boolean result;

		synchronized(enterMutex())
		{
			enterRequests--;
			result = (enterRequests > 1);

			//!: clear the requests number
			enterRequests = 0;
		}

		return result;
	}

	protected Object  enterMutex()
	{
		return enterMutex;
	}

	protected void    sendTask(long taskKey)
	{
		ExecRunEvent e = new ExecRunEvent();

		e.setService(getExecService());
		e.setTaskKey(taskKey);

		this.send(e);
	}


	/* private: configuration parameters */

	private String execService;
	private long   waitPause = 200L;


	/* private: synchronization state */

	private final Object enterMutex = new Object();
	private volatile int enterRequests;
}